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

import java.time.LocalDateTime;

public class PojoTime {

	public int i;

	public String s;

	public LocalDateTime ldt;

	public PojoTime(int i, String s, LocalDateTime ldt) {
		this.i = i;
		this.s = s;
		this.ldt = ldt;
	}

	// void a() { debug(); }
	public static PojoTime of(int i, String s, LocalDateTime ldt) {
		return new PojoTime(i, s, ldt);
	}

	private void debug() {
	}

}