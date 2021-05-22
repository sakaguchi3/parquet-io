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

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.avro.LogicalTypes;
import org.apache.avro.Schema;
import org.junit.jupiter.api.Test;

import com.github.sakaguchi3.parquet.priv.UtilsParquetIO;

public class WriteTest {

//	@Test void a() { d(); }

	/**
	 * TODO:FIX. throws exception.
	 */
	@Test
	void ngTest() {
		try {
			LogicalTypes .timestampMillis().addToSchema(Schema.create(Schema.Type.LONG));
			doTestNgVersion();
		} catch (Exception e) {
			e.printStackTrace();
			fail(e);
		}

		d();
	}

	private void doTestNgVersion() throws IOException {
		var filePathStr = System.getenv("write_path");
		Path fileJPath = Path.of(filePathStr);

		var ds = createTestDataNGVersion();
		var fileHop = UtilsParquetIO.toHOFile(fileJPath);

		var writer = new WriterTime();
		writer.write(fileHop, ds);
	}

	private List<PojoTime> createTestDataNGVersion() {
		var ldt = LocalDateTime.now();
		return List.of(PojoTime.of(1, "1s", ldt) //
				, PojoTime.of(2, "2s", ldt) //
				, PojoTime.of(3, "x", ldt) //
				, PojoTime.of(4, "x", ldt) //
				, PojoTime.of(5, "x", ldt) //
				, PojoTime.of(6, "x", ldt) //
				, PojoTime.of(7, "x", ldt) //
				, PojoTime.of(99, "x", ldt));
	}

//	@Test
	void okTest() {
		try {
			doTestOkVersion();
		} catch (Exception e) {
			fail(e);
		}

		d();
	}

	private void doTestOkVersion() throws IOException {
		var filePathStr = System.getenv("write_path");
		Path fileJPath = Path.of(filePathStr);

		var ds = createTestData();
		var fileHop = UtilsParquetIO.toHOFile(fileJPath);

		var writer = new WriterNoTime();
		writer.write(fileHop, ds);
	}

	private List<PojoNoTime> createTestData() {
		return List.of(PojoNoTime.of(1, "1s") //
				, PojoNoTime.of(2, "2s") //
				, PojoNoTime.of(3, "x") //
				, PojoNoTime.of(4, "x") //
				, PojoNoTime.of(5, "x") //
				, PojoNoTime.of(6, "x") //
				, PojoNoTime.of(7, "x") //
				, PojoNoTime.of(8, "x") //
				, PojoNoTime.of(9, "x") //
				, PojoNoTime.of(10, "x") //
				, PojoNoTime.of(11, "x") //
				, PojoNoTime.of(99, "99s"));
	}

	private void d() {
	}

}
