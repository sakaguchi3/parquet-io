/**
 * Copyright 2021. sakaguchi3, https://github.com/sakaguchi3
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
package com.github.sakaguchi3.parquet.test3;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleSupplier;
import java.util.stream.Collectors;

import org.apache.hadoop.fs.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.sakaguchi3.parquet.api.ParquetWriterCsvAbstract;

import lombok.Builder;
import lombok.Cleanup;
import lombok.Getter;

public class Csv2Parquet extends ParquetWriterCsvAbstract<SampleEntity> {

	private static Logger log = LogManager.getLogger();

	/**
	 * 
	 * @param readInfo  read file info
	 * @param writeInfo write file info
	 * @return parquet file list
	 */
	public List<java.nio.file.Path> readCsvs(ParamInputFile readInfo, ParamOutputFile writeInfo) {

		var readDirPath = readInfo.getInputDir();
		var readMaxSize = readInfo.getFileSize();
		if (!Files.isDirectory(readDirPath)) {
			log.error("not directory. {}", readDirPath);
			return List.of();
		}

		var writeDIrPath = writeInfo.getOutDir();
		if (!Files.isDirectory(writeDIrPath)) {
			log.error("not directory. {}", writeDIrPath);
			return List.of();
		}

		try {

			// list up csv file
			var readFilePaths = readCsvFromDir(readInfo);
			if (readFilePaths.isEmpty()) {
				log.trace("csv not founded. {}", readDirPath);
				return List.of();
			}

			var writePaths = new ArrayList<java.nio.file.Path>();

			var readMergeRecords = new ArrayList<SampleEntity>(20_000);
			long readSize = 0;

			// read data from file 
			for (var readFilePath : readFilePaths) {

				// write condition
				if (readSize > readMaxSize) {
					var writePath = createTmpParquetFile(writeInfo);
					writePaths.add(writePath);
					var writeHOF = toHOFile(writePath);
					write(writeHOF, readMergeRecords);

					// reset
					readSize = 0;
					readMergeRecords.clear();
				}

				readSize += Files.size(readFilePath);
				var readRecordLst = readCsv(readFilePath);
				readMergeRecords.addAll(readRecordLst);
			}

			// write condition
			if (!readMergeRecords.isEmpty()) {
				var writePath = createTmpParquetFile(writeInfo);
				writePaths.add(writePath);

				var writeHOF = toHOFile(writePath);
				write(writeHOF, readMergeRecords);

				readSize = 0;
				readMergeRecords.clear();
			}

			DoubleSupplier totalSizeCsv = () -> fileSize(readFilePaths);
			DoubleSupplier totalSizePar = () -> fileSize(writePaths);

			log.info("total size. csv:{}, parquet:{}", totalSizeCsv, totalSizePar);

			return writePaths;

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return List.of();
		}
	}

	protected double fileSize(List<java.nio.file.Path> filePath) {
		return filePath.stream() //
				.mapToLong(v -> {
					try {
						return Files.size(v);
					} catch (Exception e) {
						log.warn("fi le size error. filename:" + v.getFileName(), e);
						return 0;
					}
				}) //
				.sum();
	}

	protected java.nio.file.Path createTmpParquetFile(ParamOutputFile o) throws IOException {
		var dir = o.getOutDir();
		var prefix = o.getOutFilePrefix();
		var suffix = o.getOutFileSuffix();

		return Files.createTempFile(dir, prefix, suffix);
	}

	protected List<java.nio.file.Path> readCsvFromDir(ParamInputFile i) throws IOException {
		var dir = i.getInputDir();
		var regex = i.getInputFilenameRegex();
		@Cleanup
		var stream = Files.list(dir);
		return stream //
				.filter(v -> v.getFileName().toString().matches(regex))//
				.collect(Collectors.toList());
	}

	@Builder
	@Getter
	public static class ParamInputFile {
		final java.nio.file.Path inputDir;
		final String inputFilenameRegex;
		final double fileSize;
	}

	@Builder
	@Getter
	public static class ParamOutputFile {
		final java.nio.file.Path outDir;
		final String outFilePrefix;
		final String outFileSuffix;
	}

	void jj() {
		Path hp;
	}

}
