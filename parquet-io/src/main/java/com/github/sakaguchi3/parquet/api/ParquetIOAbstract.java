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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.avro.reflect.ReflectData;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.avro.AvroParquetReader;
import org.apache.parquet.avro.AvroParquetWriter;
import org.apache.parquet.column.ParquetProperties;
import org.apache.parquet.hadoop.ParquetFileWriter;
import org.apache.parquet.hadoop.ParquetFileWriter.Mode;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.util.HadoopInputFile;
import org.apache.parquet.io.InputFile;
import org.apache.parquet.io.OutputFile;

public abstract class ParquetIOAbstract<T> extends ParquetWriterBaseAbstract<T> {

	public List<T> read(InputFile file) throws IOException {

		var ret = new ArrayList<T>(100_000);

		try (var reader = AvroParquetReader.<T>builder(file) //
				.withConf(conf) //
				.build();) {

			T e;
			while ((e = reader.read()) != null) {
				ret.add(e);
			}

			return ret;
		}

	}

	public void write(OutputFile file, List<T> records) throws IOException {
		try (var writer = AvroParquetWriter.<T>builder(file) //
				.withConf(conf) //
				.withSchema(schema) //
				.withDataModel(ReflectData.get()) //
				.withCompressionCodec(compress) //
				.withWriteMode(Mode.OVERWRITE) //
				.build();) {

			for (var data : records) {
				writer.write(data);
				d();
			}
		}
	}

	public void append(OutputFile srcFile, List<Path> tarFiles) throws IOException {
		final var rowGroupSize = ParquetProperties.DEFAULT_PAGE_ROW_COUNT_LIMIT;
		final var maxPaddingSize = ParquetWriter.MAX_PADDING_SIZE_DEFAULT;
		final var columnIndexTruncateLength = ParquetProperties.DEFAULT_COLUMN_INDEX_TRUNCATE_LENGTH;
		final var statisticsTruncateLength = ParquetProperties.DEFAULT_STATISTICS_TRUNCATE_LENGTH;
		final var pageWriteChecksumEnabled = true;

		final var metaData = ParquetFileWriter.mergeMetadataFiles(tarFiles, conf).getFileMetaData();

		var writer = new ParquetFileWriter( //
				srcFile, //
				messageType, //
				Mode.CREATE, //
				rowGroupSize, //
				maxPaddingSize, //
				columnIndexTruncateLength, //
				statisticsTruncateLength, //
				pageWriteChecksumEnabled);

		writer.start();

		for (var hadoopPath : tarFiles) {
			var hadoopFile = HadoopInputFile.fromPath(hadoopPath, conf);
			writer.appendFile(hadoopFile);
			d();
		}

		writer.end(metaData.getKeyValueMetaData());

		d();
	}

	protected static void d() {
	};
}
