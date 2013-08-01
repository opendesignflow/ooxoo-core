OOXOO 3 : XML to Java Objects binding
======================================

## Introduction / History

One of the popular tools to process XML is the Jaxb data binding interface [JAXB] , that allows a straightway conversion from pure XML to Java objects (and reversed), totally eliminating the need to search through the structure for the data we expect. The OOXOO processor is a kind of such a translation tool, developed at a time where the Jaxb reference implementations were suffering performances problems, and integrated as standard translation mean into Idyria's products and OpenSource projects.

The 1.x release series of OOXOO were a JAXB-like implementation, just performing XML/Java translation with raw datatypes. In this presentation of the OOXOO 3 series, it will be shown how an original architecture can help improve the integration of an XML translator depending on the application context.

## Data / Application Binding

One of the most unsatisfying feature of most data binding frameworks, like Object to Relational, or XML to objects, is the lack of a correct coupling/decoupling stage between appplication and data binding layer.
As a matter of fact, developpers are always sailing between two seas:

- Object to relational is quite constraning because of special relations between tables/objects that require to be working under running framework
- XML binding provides good structured data locality, but can become high memory consuming,  and is limited to a simple object tree.
- The developer still has to fully manage its special needs and accomodate to the data binding layer.

OOXOO offers a solution to this problem, by defining a generic flexible data to objects binding interface, which is used by the API itself to provide the basic tree objects-data serilisation/deserialisation.

## The Buffer Chain and data unit

The basic building component of OOXOO is the Buffer.
A Buffer is simply an object, that can link in a chain to a previous and a next buffer.
Two methods of Buffer are responsible for the serialisation/deserialisation process:

- streamOut: The Buffer gets a DataUnit object, containing the serialisation data, and passes it to the next Buffer in chain
- streamIn:  The Buffer gets a DataUnit object, containing the deserialised data, and passes it to the previous buffer

By implementing its own Buffer class, and overriding the streamOut/streamIn methods, the user can add any kind of logic into the data binding process, while still not directly interfering with OOXOO base components

Example: 

## Structured Stream

