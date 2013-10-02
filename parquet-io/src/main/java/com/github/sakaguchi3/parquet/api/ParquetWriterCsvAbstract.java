/**
 * Copyright 2021. sakaguchi3, https://github.com/sakaguchi3/java_project3
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

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.parquet.hadoop.util.HadoopInputFile;
import org.apache.parquet.hadoop.util.HadoopOutputFile;

import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

public class ParquetWriterCsvAbstract<T> extends ParquetWriteAbstract<T> {

	protected static final Logger log = LogManager.getLogger();

	public void toCsv( //
			java.nio.file.Path readParquet, //
			java.nio.file.Path writeCsv) throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {

		// read parquet
		var readHPath = toHadoopPath(readParquet);
		var readHIFile = HadoopInputFile.fromPath(readHPath, conf);
		var readLst = read(readHIFile);

		// write csv
		writeCsv(writeCsv, readLst);
	}

	public void toParquet( //
			java.nio.file.Path readCsv, //
			java.nio.file.Path writeParquet) throws IOException {

		// read csv
		var readLst = readCsv(readCsv);

		// write parquet
		var writeFileHPath = toHadoopPath(writeParquet);
		var writeFileHOFile = HadoopOutputFile.fromPath(writeFileHPath, conf);
		write(writeFileHOFile, readLst);
	}

	public void writeCsv(java.nio.file.Path csvFile, List<T> csvEntityLst) //
			throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {

		try (//
				var bw = Files.newBufferedWriter(csvFile, CREATE, APPEND);
				var cw = new CSVWriter(bw);) {

			StatefulBeanToCsv<T> beanToCsv = new StatefulBeanToCsvBuilder<T>(cw) //
					.withApplyQuotesToAll(true) //
					.build();
			beanToCsv.write(csvEntityLst);
		}
	}

	public List<T> readCsv(java.nio.file.Path csvFile) throws IOException {
		Class<T> clazz = getType();

		try (BufferedReader r = Files.newBufferedReader(csvFile, StandardCharsets.UTF_8)) {
			var builder = new CsvToBeanBuilder<T>(r);
			List<T> remoteEntityList = builder.withType(clazz).build().parse();

			return remoteEntityList;
		}
	}

	protected static void d() {
	};
}
