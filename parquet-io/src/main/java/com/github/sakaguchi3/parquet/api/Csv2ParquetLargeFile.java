/**
 * Copyright 2021. sakaguchi3, https://github.com/sakaguchi3/parquet-io
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.sakaguchi3.parquet.api;

import static com.github.sakaguchi3.parquet.priv.UtilsParquetIO.toHadoopPath;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

import java.io.IOException;
import java.nio.file.Files;

import org.apache.avro.reflect.ReflectData;
import org.apache.parquet.avro.AvroParquetReader;
import org.apache.parquet.avro.AvroParquetWriter;
import org.apache.parquet.hadoop.ParquetFileWriter.Mode;
import org.apache.parquet.hadoop.util.HadoopInputFile;

import com.github.sakaguchi3.parquet.priv.UtilsParquetIO;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

public class Csv2ParquetLargeFile<T> extends ParquetIOAbstract<T> implements Csv2Parquet {

	public void toCsv( //
			java.nio.file.Path readParquet, //
			java.nio.file.Path writeCsv) throws IOException {

		// read parquet
		var readHPath = toHadoopPath(readParquet);
		var readHIFile = HadoopInputFile.fromPath(readHPath, conf);

		try ( //
				var readerPar = AvroParquetReader.<T>builder(readHIFile) //
						.withConf(conf) //
						.build();
				var writerBuf = Files.newBufferedWriter(readParquet, CREATE, APPEND);
				var writerCsv = new CSVWriter(writerBuf);) {

			StatefulBeanToCsv<T> beanToCsv = new StatefulBeanToCsvBuilder<T>(writerCsv) //
					.withApplyQuotesToAll(true) //
					.build();

			T record;
			while ((record = readerPar.read()) != null) {
				try {
					beanToCsv.write(record);
				} catch (Exception e) {
					throw new IOException(e);
				}
			}
		}
	}

	public void toParquet(//
			java.nio.file.Path readCsv, //
			java.nio.file.Path writeParquet) throws IOException {

		var hop = UtilsParquetIO.toHOFile(writeParquet);

		try ( //
				var br = Files.newBufferedReader(readCsv);
				var writer = AvroParquetWriter.<T>builder(hop) //
						.withConf(conf) //
						.withSchema(schema) //
						.withDataModel(ReflectData.get()) //
						.withCompressionCodec(compress) //
						.withWriteMode(Mode.OVERWRITE) //
						.build();) {

			var clazz = getType();

			var it = new CsvToBeanBuilder<T>(br).withType(clazz) //
					.build() //
					.iterator();
			while (it.hasNext()) {
				var e = it.next();
				writer.write(e);
			}
		}
	}

}
