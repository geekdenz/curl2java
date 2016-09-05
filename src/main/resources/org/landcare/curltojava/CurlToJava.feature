# some comment
# language: en
Feature: curl to java
  The application can convert all curl commands to java code and
  execute it, returning connection or response objects.

  Scenario: 
    Given The curl command
      """
      curl 'http://echo.jsontest.com/key/value/one/two'
      """
    Then The response should contain the property "key" with value "value"

  Scenario: 
    Given The curl command
      """
      curl http://scooterlabs.com/echo.json?foo=bar -d 'hello=world&body=json'
      """
    Then The response's "request" property should contain the property "foo" with value "bar"
    And The response's "request" property should contain the property "hello" with value "world"
    And The response's "request" property should contain the property "body" with value "json"

  Scenario: 
    Given The curl command
      """
      curl https://dcoder.nz/echo/ -d '{"hello":"world","body":"json"}'
      """
    Then The response should contain the property "hello" with value "world"
    And The response should contain the property "body" with value "json"

  Scenario: 
    Given The curl command
      """
      curl https://dcoder.nz/echo/ --data 'polygon=hello you'
      """
    Then The response should contain "hello you"

  Scenario: 
    Given The curl command
      """
      curl https://dcoder.nz/echo/ --data '{"name":[{},{"prop":"hooray"}]}'
      """
    Then The JSON path "name[1].prop" should contain the value "hooray"