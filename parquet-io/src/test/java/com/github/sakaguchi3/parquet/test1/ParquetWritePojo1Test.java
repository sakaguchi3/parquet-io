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
package com.github.sakaguchi3.parquet.test1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.avro.AvroReadSupport;
import org.apache.parquet.avro.ReflectDataSupplier;
import org.apache.parquet.hadoop.util.HadoopInputFile;
import org.apache.parquet.hadoop.util.HadoopOutputFile;
import org.junit.jupiter.api.Test;



public class ParquetWritePojo1Test {

	static final Configuration conf = new Configuration();

	static {
		conf.setBoolean(AvroReadSupport.AVRO_COMPATIBILITY, false);
		AvroReadSupport.setAvroDataSupplier(conf, ReflectDataSupplier.class);
	}

	ParquetWriterPojo1 pw = new ParquetWriterPojo1();

	List<Path> searchFile(java.nio.file.Path dirPath, String filenameRegex) throws IOException {
		try (var files = Files.list(dirPath);) {

			return files.filter(v -> v.getFileName().toString().matches(filenameRegex)) //
					.map(v -> new Path(v.toUri())) //
					.collect(Collectors.toList());
		}
	}

	@Test
	void append() {
		d();
		var tarDirStr = System.getenv("parquet_dir");
		var tarDirPath = Paths.get(tarDirStr);

		var srcDirStr = System.getenv("parquet_file_append");
		var srcDirPath = new Path(srcDirStr);

		try {
			var targetFiles = searchFile(tarDirPath, ".*\\.parquet");
			var srcDirHadoopFile = HadoopOutputFile.fromPath(srcDirPath, conf);
			pw.append(srcDirHadoopFile, targetFiles);
			d();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	void write() {
		d();
		var fileStr = System.getenv("parquet_file");
		var filePath = new Path(fileStr);

		var dataList = List.of( //
				new Pojo1("bx", 0), //
				Pojo1.builder().name("z").age(8).build(), //
				new Pojo1("z", 20), //
				new Pojo1("llllllc", 9990));

		try {
			var fileHadoopFile = HadoopOutputFile.fromPath(filePath, conf);
			pw.write(fileHadoopFile, dataList);
			d();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	void read() {
		d();
		var dir = System.getenv("parquet_file");
		var p = new Path(dir);

		try {
			var file = HadoopInputFile.fromPath(p, conf);
			var ans = pw.read(file);
			d();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void d() {
	};
}
