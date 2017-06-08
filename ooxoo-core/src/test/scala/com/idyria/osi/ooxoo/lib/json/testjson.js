/*-
 * #%L
 * Core runtime for OOXOO
 * %%
 * Copyright (C) 2006 - 2017 Open Design Flow
 * %%
 * This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

var str = "\"Test\":{\"_a_attr1\": \"Val\", \"SimpleElement\":\"Test\",\"MultipleElement\":\"1\",\"MultipleElement\":\"2\",\"MultipleElement\":\"3\",\"SubTest\":{\"SubSubTest\":[{\"Path\":\"%2Flocal%2Fhome%2Frleys%2Fgit%2Fextoll2%2Ftourmalet-tester%2Fwww-inputdata%2Fi2c.sscript\"},{\"Path\":\"%2Flocal%2Fhome%2Frleys%2Fgit%2Fextoll2%2Ftourmalet-tester%2Fwww-inputdata%2Fi2c.sscript\"}]},\"Action\":\"stop\",\"enumelt\":\"stopped\"}"

	var str = "\"Test\":{\"_a_attr1\": \"Val\", \"SimpleElement\":\"Test\",\"MultipleElement\":[\"1\",\"2\",\"3\"],\"SubTest\":{\"SubSubTest\":[{\"Path\":\"%2Flocal%2Fhome%2Frleys%2Fgit%2Fextoll2%2Ftourmalet-tester%2Fwww-inputdata%2Fi2c.sscript\"},{\"Path\":\"%2Flocal%2Fhome%2Frleys%2Fgit%2Fextoll2%2Ftourmalet-tester%2Fwww-inputdata%2Fi2c.sscript\"}]},\"Action\":\"stop\",\"enumelt\":\"stopped\"}"
	
//str = '"Test":{ "a" : "V"}'
print("Hi");

//var obj = eval("{"+ str + "}")
	
var test = JSON.parse("{" + str + "}");

print("Res: "+test.Test.MultipleElement[0]);
print("Res: "+test.Test.MultipleElement[1]);
print("Res: "+test.Test.MultipleElement[2]);

print("Res: "+test.Test.SubTest.SubSubTest[0].Path);
print("Res: "+test.Test.SubTest.SubSubTest[1].Path);
