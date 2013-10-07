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

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.parquet.hadoop.util.HadoopInputFile;
import org.apache.parquet.hadoop.util.HadoopOutputFile;

import com.github.sakaguchi3.parquet.priv.UtilsParquetIO;

public class Csv2ParquetNormal<T> extends ParquetIOAbstract<T> implements Csv2Parquet {

	protected static final Logger log = LogManager.getLogger();

	public void toCsv( //
			java.nio.file.Path readParquet, //
			java.nio.file.Path writeCsv) throws IOException {

		// read parquet
		var readHPath = toHadoopPath(readParquet);
		var readHIFile = HadoopInputFile.fromPath(readHPath, conf);
		var readLst = read(readHIFile);

		try {
			// write csv
			UtilsParquetIO.writeCsv(writeCsv, readLst);
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	public void toParquet( //
			java.nio.file.Path readCsv, //
			java.nio.file.Path writeParquet) throws IOException {

		Class<T> genericsType = getType();

		// read csv
		var readLst = UtilsParquetIO.readCsv(genericsType, readCsv);

		// write parquet
		var writeFileHPath = toHadoopPath(writeParquet);
		var writeFileHOFile = HadoopOutputFile.fromPath(writeFileHPath, conf);

		write(writeFileHOFile, readLst);
	}

}
