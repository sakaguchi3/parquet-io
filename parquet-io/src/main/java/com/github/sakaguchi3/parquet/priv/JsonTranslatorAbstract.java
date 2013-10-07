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

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.ParameterizedType;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class JsonTranslatorAbstract<T> {

	private Class<T> clazz;

	@SuppressWarnings("unchecked")
	public JsonTranslatorAbstract(Class<? extends JsonTranslatorAbstract<?>> subclass) {
		ParameterizedType pType = (ParameterizedType) subclass.getGenericSuperclass();
		clazz = (Class<T>) pType.getActualTypeArguments()[0];
	}

	public T fromJSON(String json) throws JsonMappingException, JsonParseException, IOException {
		StringReader reader = new StringReader(json);

		ObjectMapper mapper = new ObjectMapper();
		T ret = mapper.readValue(reader, clazz);

		return ret;
	}

	public String toJSON(T value) throws IOException {
		StringWriter writer = new StringWriter();

		MappingJsonFactory factory = new MappingJsonFactory();
		JsonGenerator generator = factory.createJsonGenerator(writer);

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.writeValue(generator, value);

		return writer.toString();
	}

	protected Class<?> getType() {
		return clazz;
	}

}
