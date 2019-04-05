# Enum Deserializer 

This library smooths out differences in the way that different versions of jackson deserialize enums
to ease the process of upgrading between versions. In jackson-databind-2.3.3, an int can be successfully
deserialized into an enum while in jackson-databind-2.5.4 and newer, it can't. The custom deserializer
handles the following cases:

 - Deserializing from int
	 - If there is a @JsonCreator method that takes in int, use that
	 - If there isn't and there is a single field in the enum of type int, match based on that
- Deserializing from string
	- If the string parses to an int, parse it and use the int creation method instead
	- If there isn't and there is a @JsonCreator method that takes in string, use that
	- If there isn't match based on the name of the enum variants

# How to build locally
```
mvn clean install
```
