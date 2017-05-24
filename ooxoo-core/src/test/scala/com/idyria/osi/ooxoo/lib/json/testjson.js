
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