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

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Util {

	private static Logger log = LogManager.getLogger();

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

	public static Path moveFileToDir(Path fromFile, Path toDir) {
		var toFile = Paths.get(toDir.toString(), fromFile.getFileName().toString());
		return toFile;
	}

}
