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
package com.github.sakaguchi3.parquet.priv;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.avro.AvroReadSupport;
import org.apache.parquet.avro.ReflectDataSupplier;
import org.apache.parquet.hadoop.util.HadoopInputFile;
import org.apache.parquet.hadoop.util.HadoopOutputFile;

import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import lombok.Getter;

public class UtilsParquetIO {

	@Getter
	public static final Configuration hadoopConf = initHdoopConf();

	public static final double KiB = Math.pow(1024, 1);
	public static final double MiB = Math.pow(1024, 2);
	public static final double GiB = Math.pow(1024, 3);
	public static final double TiB = Math.pow(1024, 4);
	public static final double PiB = Math.pow(1024, 5);
	public static final double EiB = Math.pow(1024, 6);

	public static String fileSize(double fileSizeByte) {
		String.format("%.1f", fileSizeByte);

		if (fileSizeByte < KiB) {
			return String.format("%.1f[B]", fileSizeByte);
		}
		if (fileSizeByte < MiB) {
			return String.format("%.1f[KiB]", fileSizeByte / KiB);
		}
		if (fileSizeByte < GiB) {
			return String.format("%.1f[MiB]", fileSizeByte / MiB);
		}
		if (fileSizeByte < TiB) {
			return String.format("%.1f[GiB]", fileSizeByte / GiB);
		}
		if (fileSizeByte < PiB) {
			return String.format("%.1f[TiB]", fileSizeByte / TiB);
		}
		if (fileSizeByte < EiB) {
			return String.format("%.1f[PiB]", fileSizeByte / PiB);
		}

		return String.format("%.1f[EiB]", fileSizeByte / EiB);
	}

	public static java.nio.file.Path moveFileToDir( //
			java.nio.file.Path fromFile, //
			java.nio.file.Path toDir) {
		var toFile = Paths.get(toDir.toString(), fromFile.getFileName().toString());
		return toFile;
	}

	public static Path toHadoopPath(java.nio.file.Path javaPath) {
		return new Path(javaPath.toUri());
	}

	public static java.nio.file.Path toJavaPath(Path hadoopPath) {
		return Paths.get(hadoopPath.toUri());
	}

	public static HadoopInputFile toHIFile(java.nio.file.Path javaPath) throws IOException {
		var hadoopPath = toHadoopPath(javaPath);
		var hadoopInputFile = HadoopInputFile.fromPath(hadoopPath, hadoopConf);
		return hadoopInputFile;
	}

	public static HadoopOutputFile toHOFile(java.nio.file.Path javaPath) throws IOException {
		var hadoopPath = toHadoopPath(javaPath);
		var hadoopOutputPath = HadoopOutputFile.fromPath(hadoopPath, hadoopConf);
		return hadoopOutputPath;
	}

	private static Configuration initHdoopConf() {
		var hadoopConf = new Configuration();
		hadoopConf.setBoolean(AvroReadSupport.AVRO_COMPATIBILITY, false);
		AvroReadSupport.setAvroDataSupplier(hadoopConf, ReflectDataSupplier.class);
		return hadoopConf;
	}

	public static <T> void writeCsv( //
			java.nio.file.Path csvFile, //
			List<T> csvEntityLst) //
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

	public static <T> List<T> readCsv( //
			Class<T> clazz, //
			java.nio.file.Path csvFile) throws IOException {

		try (BufferedReader r = Files.newBufferedReader(csvFile, StandardCharsets.UTF_8)) {
			var builder = new CsvToBeanBuilder<T>(r);
			List<T> remoteEntityList = builder.withType(clazz).build().parse();

			return remoteEntityList;
		}
	}
}
