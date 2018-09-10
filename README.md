### The API automation for the code challange 

#### Prerequisites:

This project requires maven(3.X) to be installed and configured . For more details please refer to https://maven.apache.org/install.html

#### Running the project
1. Navigate to the automation-api-challenge folder (that contains the pom.xml) in the command prompt
2. type __`mvn clean test`__  . The automation will kick in running all the cases

#### Notes 
1. This project uses OKHttp client and does not use a framework approach . So detailed configuration for different environments and other features are not available at this moment !

#### A note on assertion messages 
You might have noticed that the testNG AssertTrue messages are positive , This is done for a reason . For example consider the folowing line 
`Assert.assertTrue(200 == API_CREATED_CODE, "The API gave a 200 response --");`
When this fails , TestNG will report as follows 

__java.lang.AssertionError: The API gave a 200 response -- expected [true] but found [false]__
