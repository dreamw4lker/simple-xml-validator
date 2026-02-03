import base64

with open('input.txt', 'r') as input_file:
  coded_string = input_file.read()

decoded = base64.b64decode(coded_string)

with open('out.xml', 'w', encoding="utf-8") as output_file:
  output_file.write(decoded.decode("utf-8"))
