/*-
 * #%L
 * Core runtime for OOXOO
 * %%
 * Copyright (C) 2006 - 2017 Open Design Flow
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package org.odfi.ooxoo.core.buffers.compress

import org.odfi.ooxoo.core.buffers.datatypes.compress.ZipString
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class CompressedBufferTest extends AnyFunSuite with  Matchers  {
  
  
  test("ZipString test") {
    
    
    val testString = """aXDIgHeUYJEs16lfEdeP
jASlyVnRcao2gW4NUbSN
lw24ZJAJJvhHqUAmJvvz
kjBs2Brq9CJPRcdLA26p
TfCPs7c98vbynlLugkuI
aKc09ya5KqfCZFbsnWUT
T5Wo7bvDFRNElfr5gNbj
PMZvrB2avBIpIJDclDN6
lkfHIJ6cGnAqIvDOlGMa
WS8v3spfOpk7YscWlGnO
2OlynD1MXpy3Gtn13plp
OX9uOFZSdZXcC8OIUlhx
mRYXHHFq0YgjxqYv7EA9
UY0ILT3IccJ5WnP81AH8
diirxePr49KLdevFpsC9
jQ6BFMIpbeagBBTcbY3x
qwAaFZlfOqhM5S9mKwz8
mRFzKKi8MTO5KFEXmNnH
jTQ5rJyO6EkTlF7DzNhe
aL98F83xK2rWdIgMwu2i
I4qdHj3SfrmHV7YURL49
WWDcZDYCiX9CiMm0dDRa
KdjzoN0uYFvPQZCNC1tX
bsrfdpkscGxzsq5CGGM5
fCzzEEstYcL9M14glOjR
6aqIWqAE4aZdvzFIB1W9
tTLzpVkKkJGvNUvVA56w
M3ef8TCxrxrxJ6zmXdze
05xdm8w4E2qL38YpFG4H
8JjRWdExx07zxd1GjehZ
oreR0sNzMyorBm5SoBZ0
APRjNzSIldsUyw8rDENB
HxCWNJ6OGLbZII5tOOVC
CCehx1AnfCSdUu9dZWt1
8wm6pp2JtbeHLb9SBDzL
W3QT4VWlojJtKamoRumL
YKvWykm7FtwbUeDY3QH1
xr9mFj8NYB7Xovg5nkSG
3dQU2TKCL8y252HOX2fd
dibyn35dUb3BEkkxkLx2
fa4ioXirVFWNWSBHhD0N
tiFi5iGa4xbCBBT1La5O
I5cog73EanIuq1xwXQ9f
aNB22HKhpOhSMZz3uYk8
UakqGNpBNGEsojNovpC0
q1GiqKi1U7ltfk9J8TPc
6NYt0VwYGt7HiJ3b1k1d
QiYaOfom9p1Et8u6sFQR
As3MC78FqEpsObH7ZSkl
tRhu0hogJUEpqU7DjfUY
U5odsDdzrYHCp1zNSXEY
D4jlgic0PDqC6AkVv2Ew
Gon89KogTmAcjuk1rZSZ
8XD80umwlRRHSRGyttUM
MvZPdiKTfHnJ9urKn6X6
HgnDCUASkOuu8S1CMTGc
XpCM2pS6zFEQfMPcuJhx
lsBJkE8TJXlRZbtFxHr0
wAEwi1xzEhU4lphcXbaV
MLcqkvczSX2z3YcvXdYf
o23A2c7Ey28DbtqnoPi5
P4EypPWQKpMKVRfKL9WT
DIbMR0YnLd848xlcVIzh
8aQ6EfZUPMC31BRvKRru
LhZZ2NUbiBUz7NN4hd6R
DgP0fNgtYXBZEjalY6Z8
I7MJ3KmdALLhYEuTOzOu
c1ApTErCx0uXzHUAcHMx
iQDAFuNZNUAWH81IyShp
edlE78r8aSFO7Dd0Ty14
D4FpDp2IpTCxVwInzF4P
CbLkeeK48NeAK2fLeq24
6g5kKVDKE8h2nF6G3b5n
ydxFuaR58gVHRbnFCgHu
ByRBLtWQc9RhYcixT6u9
AqnfwQ72b741bPkHIeD9
hqFZolyJYS3fw1A92I5A
QPUNTzG50CBKB5lE1FQK
LOdDgQx6Ubi2BQnmPo1e
td7b42zHqyvOdkTfOPXH
E3Q1fQxIkcgddZGdRrYt
ZL0GjCgwDHyWQAvExze6
rIV0vQdIFaPvxKI4Pvah
fYynA93gHr0TZJlvWXIG
kX6scyU2QPEsx8Q2tkKL
cTS7z9rPMHW7ltv1DBoW
eWOtsZ1Ept5RvN9KmdDI
Ybd0p1ZVmR3KXuFm7yHO
3ZPsgNz331XLcOBt7jtJ
4DwbHkb4dyKcEd3eDXQO
vrVkUc4I0KfPyO2EYYkY
qwoAPZJJiJdLJ0SLTeLG
V3naoqFmuS32MjzaAbtg
PoKLUnTB5Z8himyWcbG4
vIC4DSgwevBvvV4TMWLu
rD3mCfiJkFpL6fypayRv
SAEbtLSTttQpAlwPbrwk
I2ATxPrjduAoFqgD2Ksu
hL5YBGrK7RYBlNmYucbc
zhtjC9jvo4rJwE0h4lmNaXDIgHeUYJEs16lfEdeP
jASlyVnRcao2gW4NUbSN
lw24ZJAJJvhHqUAmJvvz
kjBs2Brq9CJPRcdLA26p
TfCPs7c98vbynlLugkuI
aKc09ya5KqfCZFbsnWUT
T5Wo7bvDFRNElfr5gNbj
PMZvrB2avBIpIJDclDN6
lkfHIJ6cGnAqIvDOlGMa
WS8v3spfOpk7YscWlGnO
2OlynD1MXpy3Gtn13plp
OX9uOFZSdZXcC8OIUlhx
mRYXHHFq0YgjxqYv7EA9
UY0ILT3IccJ5WnP81AH8
diirxePr49KLdevFpsC9
jQ6BFMIpbeagBBTcbY3x
qwAaFZlfOqhM5S9mKwz8
mRFzKKi8MTO5KFEXmNnH
jTQ5rJyO6EkTlF7DzNhe
aL98F83xK2rWdIgMwu2i
I4qdHj3SfrmHV7YURL49
WWDcZDYCiX9CiMm0dDRa
KdjzoN0uYFvPQZCNC1tX
bsrfdpkscGxzsq5CGGM5
fCzzEEstYcL9M14glOjR
6aqIWqAE4aZdvzFIB1W9
tTLzpVkKkJGvNUvVA56w
M3ef8TCxrxrxJ6zmXdze
05xdm8w4E2qL38YpFG4H
8JjRWdExx07zxd1GjehZ
oreR0sNzMyorBm5SoBZ0
APRjNzSIldsUyw8rDENB
HxCWNJ6OGLbZII5tOOVC
CCehx1AnfCSdUu9dZWt1
8wm6pp2JtbeHLb9SBDzL
W3QT4VWlojJtKamoRumL
YKvWykm7FtwbUeDY3QH1
xr9mFj8NYB7Xovg5nkSG
3dQU2TKCL8y252HOX2fd
dibyn35dUb3BEkkxkLx2
fa4ioXirVFWNWSBHhD0N
tiFi5iGa4xbCBBT1La5O
I5cog73EanIuq1xwXQ9f
aNB22HKhpOhSMZz3uYk8
UakqGNpBNGEsojNovpC0
q1GiqKi1U7ltfk9J8TPc
6NYt0VwYGt7HiJ3b1k1d
QiYaOfom9p1Et8u6sFQR
As3MC78FqEpsObH7ZSkl
tRhu0hogJUEpqU7DjfUY
U5odsDdzrYHCp1zNSXEY
D4jlgic0PDqC6AkVv2Ew
Gon89KogTmAcjuk1rZSZ
8XD80umwlRRHSRGyttUM
MvZPdiKTfHnJ9urKn6X6
HgnDCUASkOuu8S1CMTGc
XpCM2pS6zFEQfMPcuJhx
lsBJkE8TJXlRZbtFxHr0
wAEwi1xzEhU4lphcXbaV
MLcqkvczSX2z3YcvXdYf
o23A2c7Ey28DbtqnoPi5
P4EypPWQKpMKVRfKL9WT
DIbMR0YnLd848xlcVIzh
8aQ6EfZUPMC31BRvKRru
LhZZ2NUbiBUz7NN4hd6R
DgP0fNgtYXBZEjalY6Z8
I7MJ3KmdALLhYEuTOzOu
c1ApTErCx0uXzHUAcHMx
iQDAFuNZNUAWH81IyShp
edlE78r8aSFO7Dd0Ty14
D4FpDp2IpTCxVwInzF4P
CbLkeeK48NeAK2fLeq24
6g5kKVDKE8h2nF6G3b5n
ydxFuaR58gVHRbnFCgHu
ByRBLtWQc9RhYcixT6u9
AqnfwQ72b741bPkHIeD9
hqFZolyJYS3fw1A92I5A
QPUNTzG50CBKB5lE1FQK
LOdDgQx6Ubi2BQnmPo1e
td7b42zHqyvOdkTfOPXH
E3Q1fQxIkcgddZGdRrYt
ZL0GjCgwDHyWQAvExze6
rIV0vQdIFaPvxKI4Pvah
fYynA93gHr0TZJlvWXIG
kX6scyU2QPEsx8Q2tkKL
cTS7z9rPMHW7ltv1DBoW
eWOtsZ1Ept5RvN9KmdDI
Ybd0p1ZVmR3KXuFm7yHO
3ZPsgNz331XLcOBt7jtJ
4DwbHkb4dyKcEd3eDXQO
vrVkUc4I0KfPyO2EYYkY
qwoAPZJJiJdLJ0SLTeLG
V3naoqFmuS32MjzaAbtg
PoKLUnTB5Z8himyWcbG4
vIC4DSgwevBvvV4TMWLu
rD3mCfiJkFpL6fypayRv
SAEbtLSTttQpAlwPbrwk
I2ATxPrjduAoFqgD2Ksu
hL5YBGrK7RYBlNmYucbc
zhtjC9jvo4rJwE0h4lmNaXDIgHeUYJEs16lfEdeP
jASlyVnRcao2gW4NUbSN
lw24ZJAJJvhHqUAmJvvz
kjBs2Brq9CJPRcdLA26p
TfCPs7c98vbynlLugkuI
aKc09ya5KqfCZFbsnWUT
T5Wo7bvDFRNElfr5gNbj
PMZvrB2avBIpIJDclDN6
lkfHIJ6cGnAqIvDOlGMa
WS8v3spfOpk7YscWlGnO
2OlynD1MXpy3Gtn13plp
OX9uOFZSdZXcC8OIUlhx
mRYXHHFq0YgjxqYv7EA9
UY0ILT3IccJ5WnP81AH8
diirxePr49KLdevFpsC9
jQ6BFMIpbeagBBTcbY3x
qwAaFZlfOqhM5S9mKwz8
mRFzKKi8MTO5KFEXmNnH
jTQ5rJyO6EkTlF7DzNhe
aL98F83xK2rWdIgMwu2i
I4qdHj3SfrmHV7YURL49
WWDcZDYCiX9CiMm0dDRa
KdjzoN0uYFvPQZCNC1tX
bsrfdpkscGxzsq5CGGM5
fCzzEEstYcL9M14glOjR
6aqIWqAE4aZdvzFIB1W9
tTLzpVkKkJGvNUvVA56w
M3ef8TCxrxrxJ6zmXdze
05xdm8w4E2qL38YpFG4H
8JjRWdExx07zxd1GjehZ
oreR0sNzMyorBm5SoBZ0
APRjNzSIldsUyw8rDENB
HxCWNJ6OGLbZII5tOOVC
CCehx1AnfCSdUu9dZWt1
8wm6pp2JtbeHLb9SBDzL
W3QT4VWlojJtKamoRumL
YKvWykm7FtwbUeDY3QH1
xr9mFj8NYB7Xovg5nkSG
3dQU2TKCL8y252HOX2fd
dibyn35dUb3BEkkxkLx2
fa4ioXirVFWNWSBHhD0N
tiFi5iGa4xbCBBT1La5O
I5cog73EanIuq1xwXQ9f
aNB22HKhpOhSMZz3uYk8
UakqGNpBNGEsojNovpC0
q1GiqKi1U7ltfk9J8TPc
6NYt0VwYGt7HiJ3b1k1d
QiYaOfom9p1Et8u6sFQR
As3MC78FqEpsObH7ZSkl
tRhu0hogJUEpqU7DjfUY
U5odsDdzrYHCp1zNSXEY
D4jlgic0PDqC6AkVv2Ew
Gon89KogTmAcjuk1rZSZ
8XD80umwlRRHSRGyttUM
MvZPdiKTfHnJ9urKn6X6
HgnDCUASkOuu8S1CMTGc
XpCM2pS6zFEQfMPcuJhx
lsBJkE8TJXlRZbtFxHr0
wAEwi1xzEhU4lphcXbaV
MLcqkvczSX2z3YcvXdYf
o23A2c7Ey28DbtqnoPi5
P4EypPWQKpMKVRfKL9WT
DIbMR0YnLd848xlcVIzh
8aQ6EfZUPMC31BRvKRru
LhZZ2NUbiBUz7NN4hd6R
DgP0fNgtYXBZEjalY6Z8
I7MJ3KmdALLhYEuTOzOu
c1ApTErCx0uXzHUAcHMx
iQDAFuNZNUAWH81IyShp
edlE78r8aSFO7Dd0Ty14
D4FpDp2IpTCxVwInzF4P
CbLkeeK48NeAK2fLeq24
6g5kKVDKE8h2nF6G3b5n
ydxFuaR58gVHRbnFCgHu
ByRBLtWQc9RhYcixT6u9
AqnfwQ72b741bPkHIeD9
hqFZolyJYS3fw1A92I5A
QPUNTzG50CBKB5lE1FQK
LOdDgQx6Ubi2BQnmPo1e
td7b42zHqyvOdkTfOPXH
E3Q1fQxIkcgddZGdRrYt
ZL0GjCgwDHyWQAvExze6
rIV0vQdIFaPvxKI4Pvah
fYynA93gHr0TZJlvWXIG
kX6scyU2QPEsx8Q2tkKL
cTS7z9rPMHW7ltv1DBoW
eWOtsZ1Ept5RvN9KmdDI
Ybd0p1ZVmR3KXuFm7yHO
3ZPsgNz331XLcOBt7jtJ
4DwbHkb4dyKcEd3eDXQO
vrVkUc4I0KfPyO2EYYkY
qwoAPZJJiJdLJ0SLTeLG
V3naoqFmuS32MjzaAbtg
PoKLUnTB5Z8himyWcbG4
vIC4DSgwevBvvV4TMWLu
rD3mCfiJkFpL6fypayRv
SAEbtLSTttQpAlwPbrwk
I2ATxPrjduAoFqgD2Ksu
hL5YBGrK7RYBlNmYucbc
zhtjC9jvo4rJwE0h4lmN"""
    
    // Compress
    //-------------
    var buffer = new ZipString
    buffer.data = testString
   
    val compressed = buffer.dataToString
    println("Compressed Size : "+compressed.length())
    
    // Decompress
    //---------------
    buffer.data = null
    buffer.dataFromString(compressed)
    buffer.decompressData
    
    println("Decompressed Size: "+buffer.data.size)
    
    assertResult(testString)(buffer.data)
    
  }
}
