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
package com.github.sakaguchi3.parquet;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.avro.Schema;

import com.github.sakaguchi3.parquet.api.ParquetWriteAbstract;

public class ParquetWriteNest extends ParquetWriteAbstract<PojoRoot> {

	private final ThreadLocalRandom r = ThreadLocalRandom.current();

	public Schema getSchema() {
		return schema;
	}

	public PojoRoot makeRoot() {
		var h = PojoRoot.builder() //
				.name("r_" + r.nextInt(100)) //
				.age(r.nextInt(30)) //
				.hoge(makeHoge()) //
				.piyo(makePiyoList()) //
				.build();

		return h;
	}

	public List<PojoPiyo> makePiyoList() {
		var cnt = r.nextInt(3, 10);
		var h = IntStream.range(0, cnt) //
				.mapToObj(__ -> makePiyo()) //
				.collect(Collectors.toList());
		return h;
	}

	public PojoPiyo makePiyo() {
		var h = new PojoPiyo("p_" + r.nextInt(15));
		return h;
	}

	public PojoHoge makeHoge() {
		var h = PojoHoge.builder()//
				.hogeName("h_" + Math.random()) //
				.build();
		return h;
	}

}
