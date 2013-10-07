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

import org.apache.avro.Schema;
import org.apache.avro.reflect.ReflectData;
import org.apache.hadoop.conf.Configuration;
import org.apache.parquet.avro.AvroSchemaConverter;
import org.apache.parquet.hadoop.metadata.CompressionCodecName;
import org.apache.parquet.schema.MessageType;

import com.github.sakaguchi3.parquet.priv.UtilsParquetIO;

public abstract class ParquetWriterBaseAbstract<T> extends GenericsTypeAbstract<T> {

	protected final Configuration conf = UtilsParquetIO.getHadoopConf();

	protected final CompressionCodecName compress = CompressionCodecName.GZIP;

	protected final MessageType messageType;
	protected final Schema schema;

	protected ParquetWriterBaseAbstract() {
		schema = ReflectData.get().getSchema(getType());
		messageType = new AvroSchemaConverter().convert(schema);
	}
}
