![SaltNPepper project](./md/img/SaltNPepper_logo2010.png)
# pepperModules-GenericXMLModules
This project provides a plugin for the linguistic converter framework Pepper (see https://github.com/korpling/pepper). Pepper is a pluggable framework to convert a variety of linguistic formats (like [TigerXML](http://www.ims.uni-stuttgart.de/forschung/ressourcen/werkzeuge/TIGERSearch/doc/html/TigerXML.html), the [EXMARaLDA format](http://www.exmaralda.org/), [PAULA](http://www.sfb632.uni-potsdam.de/paula.html) etc.) into each other. Furthermore Pepper uses Salt (see https://github.com/korpling/salt), the graph-based meta model for linguistic data, which acts as an intermediate model to reduce the number of mappings to be implemented. That means converting data from a format _A_ to format _B_ consists of two steps. First the data is mapped from format _A_ to Salt and second from Salt to format _B_. This detour reduces the number of Pepper modules from _n<sup>2</sup>-n_ (in the case of a direct mapping) to _2n_ to handle a number of n formats.

![n:n mappings via SaltNPepper](./md/img/puzzle.png)

In Pepper there are three different types of modules:
* importers (to map a format _A_ to a Salt model)
* manipulators (to map a Salt model to a Salt model, e.g. to add additional annotations, to rename things to merge data etc.)
* exporters (to map a Salt model to a format _B_).

For a simple Pepper workflow you need at least one importer and one exporter.

This project provides an importer to import data coming from a wide range of xml formats to Salt. A detailed description of that mapping can be found in section [GenericXMLImporter](#details).

## Requirements
Since the here provided module is a plugin for Pepper, you need an instance of the Pepper framework. If you do not already have a running Pepper instance, click on the link below and download the latest stable version (not a SNAPSHOT):

> Note:
> Pepper is a Java based program, therefore you need to have at least Java 7 (JRE or JDK) on your system. You can download Java from https://www.oracle.com/java/index.html or http://openjdk.java.net/ .


## Install module
If this Pepper module is not yet contained in your Pepper distribution, you can easily install it. Just open a command line and enter one of the following program calls:

### Windows

    pepperStart.bat is https://korpling.german.hu-berlin.de/saltnpepper/repository/repo/de/hu_berlin/german/korpling/saltnpepper/pepperModules/pepperModules-GenericXMLModules/1.1.2/de.hu_berlin.german.korpling.saltnpepper.pepperModules.pepperModules-GenericXMLModules_1.1.2.zip

### Linux/Unix

    bash pepperStart.sh is https://korpling.german.hu-berlin.de/saltnpepper/repository/repo/de/hu_berlin/german/korpling/saltnpepper/pepperModules/pepperModules-GenericXMLModules/1.1.2/de.hu_berlin.german.korpling.saltnpepper.pepperModules.pepperModules-GenericXMLModules_1.1.2.zip


## Usage
To use this module in your Pepper workflow, put the following lines into the workflow description file. Note the fixed order of xml elements in the workflow description file: &lt;importer/>, &lt;manipulator/>, &lt;exporter>. The GenericXMLImporter is an importer module, which can be addressed by one of the following alternatives.
A detailed description of the Pepper workflow can be found on the [Pepper project site](https://github.com/korpling/pepper). 

### a) Identify the module by name

```xml
<importer name="GenericXMLImporter" path="PATH_TO_CORPUS"/>
```

### b) Identify the module by formats
```xml
<importer formatName="xml" formatVersion="1.0" path="PATH_TO_CORPUS"/>
```

### c) Use properties
```xml
<importer name="GenericXMLImporter" path="PATH_TO_CORPUS">
  <property key="PROPERTY_NAME">PROPERTY_VALUE</key>
</importer>
```

## Contribute
Since this Pepper module is under a free license, please feel free to fork it from github and improve the module. If you even think that others can benefit from your improvements, don't hesitate to make a pull request, so that your changes can be merged.
If you have found any bugs, or have some feature request, please open an issue on github. If you need any help, please write an e-mail to saltnpepper@lists.hu-berlin.de .

## Funders
This project has been funded by the [department of corpus linguistics and morphology](https://www.linguistik.hu-berlin.de/institut/professuren/korpuslinguistik/) of the Humboldt-Universität zu Berlin, the Institut national de recherche en informatique et en automatique ([INRIA](www.inria.fr/en/)) and the [Sonderforschungsbereich 632](https://www.sfb632.uni-potsdam.de/en/). 

## License
  Copyright 2009 Humboldt University of Berlin, INRIA.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
 
  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.



##<a name="details"/>TEIImporter

General information about this importer.

### Mapping to Salt

The fact that TEI is a XML-format results in the decision to primarily
use "SStructure" while mapping TEI to Salt. There are two important
exceptions to this: Tokens("SToken" is used) and the unary "break"
elements like \<lb\> and \<pb\>(these cannot be mapped as "SStructure"
because their semantic does not fit into the hierarchy provided by XML).
Instead, "SSpan" is used. Tokens can be defined and interpreted in many
different ways and thus customization through properties deal with the
problems occuring because of this.

### Properties

Because TEI is a very complex format the behavior of the TEIImporter
depends to a great extent on the properties that the user can use to
customize the behaviour of the TEIImporter. The table ? contains an
overview of all usable properties to customize the behaviour of the
TEIImporter. The following section contains a close description to each
single property and describes the resulting differences in the mapping
to the Salt model.

| Name of property                              | Type of property | optional/mandatory | default value      |
|-----------------------------------------------|------------------|--------------------|--------------------|
| TEIImporter.DefaultTokenization               | Boolean          | optional           | false              |
| TEIImporter.SubTokenization                   | Boolean          | optional           | true               |
| TEIImporter.SurplusRemoval			| Boolean          | optional           | true               |
| TEIImporter.UnclearAsToken        		| Boolean          | optional           | true               |
| TEIImporter.ForeignAsToken          		| Boolean          | optional           | true               |
| TEIImporter.UseTokenizer| XPath            	| Boolean          | optional           | false		     |
| TEIImporter.UseTokenizerLang                  | String           | optional           | en	             |

  Name of property   Type of property   optional/ mandatory   default value
  ------------------ ------------------ --------------------- ---------------
  ?                  Boolean            optional              false
  ?                  Boolean            optional              true
  ?                  Boolean            optional              true
  ?                  Boolean            optional              true
  ?                  Boolean            optional              true
  ?                  Boolean            optional              false
  ?                  String             optional              en

  : properties to customize importer behaviour

### TEIImporter.DefaultTokenization

The user declares that there is one and only element responsible for
mapping tokens to Salt. Default is \<w\>.

### TEIImporter.SubTokenization

In this scenario, units smaller than ‘words’ exist. Elements within
\<w\> etc. are possible.

### TEIImporter.SurplusRemoval

Will text from \<surplus\> appear in Salt? If this is set "true"
(default), then \<surplus\>-text will be removed.

### TEIImporter.UnclearAsToken

Does \<unclear\> exclusively create one token annotated as "unclear"? If
this is set "true" (default), thentext in \<unclear\> will appear as a
token.

### TEIImporter.ForeignAsToken

Does \<foreign\> exclusively create one token annotated as "foreign"? If
this is set "true" (default), then text in \<foreign\> will appear as a
token.

### TEIImporter.UseTokenizer

Do you want the tokenizer to tokenize text? This option is useful, if
your TEI document contains sections of text that are not tokenized.

### TEIImporter.UseTokenizerLang

The tokenizer currently has support for four languages: English, German,
Italian, French. To choose a language, use the respective ISO 639-1
language code(en, de, it, fr). If no value or a non-supported value is
set, the tokenizer will default to English.
