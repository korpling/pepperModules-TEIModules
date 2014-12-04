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
This project has been funded by the [department of corpus linguistics and morphology](https://www.linguistik.hu-berlin.de/institut/professuren/korpuslinguistik/) of Humboldt-Universität zu Berlin, [Georgetown University](http://www.georgetown.edu/), [KOMeT](http://korpling.german.hu-berlin.de/komet/) and the [Sonderforschungsbereich 632](https://www.sfb632.uni-potsdam.de/en/). 

## License
  Copyright 2014 Humboldt-Universität zu Berlin.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
 
  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.



# TEIImporter

The TEIImporter imports data coming from [TEI-XML](http://www.tei-c.org/Guidelines/P5/) files to a [Salt](https://github.com/korpling/salt) model. This importer provides a wide range of customization possibilities via the here described set of properties. Before we talk about the possibility of customizing the mapping, we describe the general and default mapping from TEI to a Salt model.

### Mapping to Salt

The fact that TEI is a XML-format results in the decision to primarily
use tree-like structures in Salt (SStructure) that conserve hierarchies. There are two important
exceptions to this: Tokens and the unary "break"
elements like \<lb\> and \<pb\> (these cannot be mapped to trees
because their semantic does not fit into XML hierarchie).
For these elements spans (SSpan) are used. Tokens in TEI can be defined and interpreted in many
different ways and thus customization through properties deals with the
problems occuring because of this.

### Metadata
Metadata in Salt are represented by attribute-value pairs (having a name and a value). Since
metadata in TEI can occur in very deep structures like

```xml
<TEI xmlns="http://www.tei-c.org/ns/1.0">
    <teiHeader>
        <fileDesc>
            <titleStmt>
                <title>Gospel According to Mark</title>
                <author>Mark the Evangelist</author>
            </titleStmt>
        </fileDesc>
    </teiHeader>
    ...
```

they need to be flattened, e.g. to 

```
/fileDesc/titleStmt/title = "Gospel According to Mark"
/fileDesc/titleStmt/author = "Mark the Evangelist"
```

A metadata key in Salt like can only be used once. If for some reason (e.g. by using a property)
a metadata name is used for a second time, the TEIImporter will ignore the second
usage and give a warning.

#### Default Metadata Mapping
Long metadata names can be very annoying, therefore they can be shortened by using the
[metadata.rename](#mr2) property.
In addition to that, the following metadata names are shortened by default:

| TEI-Path          | Shortened Salt Name|
|-------------------|--------------------|
|/fileDesc/titleStmt/title|title         |
|/fileDesc/titleStmt/author|author       |

### Properties

Because TEI is a very complex format the behaviour of the TEIImporter
depends to a great extent on the properties that the user can use to
customize the behaviour of the TEIImporter. The following table contains an
overview of all usable properties to customize the behaviour of the
TEIImporter. The following section contains a close description to each
single property and describes the resulting differences in the mapping
to the Salt model.

| Name of property                              | Type of property | optional/mandatory | default value      |
|-----------------------------------------------|------------------|--------------------|--------------------|
| [annotation.default.remove](#adr)         | Boolean          | optional           | false              |
| [annotation.element.rename](#aer)         | String           | optional           |                    |
| [annotation.namespace](#an)               | Boolean          | optional           | false              |
| [annotation.token.span](#ats)             | Boolean          | optional           | false              |
| [annotation.value.rename](#avr)           | String           | optional           |                    |
| [element.foreign.token](#eft)        		| Boolean          | optional           | true               |
| [element.generic.attribute](#ega)         | Boolean          | optional           | false              |
| [element.generic.node](#egn)              | span \| struct \| false| optional     | struct             |
| [element.surplus.remove](#esr)	        | Boolean          | optional           | true               |
| [element.unclear.token](#eut)        		| Boolean          | optional           | true               |
| [metadata.lastpartonly](#ml)              | Boolean          | optional           | false              |
| [metadata.redundant.remove](#mrr)         | Boolean          | optional           | false	             |
| [metadata.remove](#mr1)                   | Boolean          | optional           | false              |
| [metadata.remove.list](#mrl)              | String           | optional           |                    |
| [metadata.rename](#mr2)                   | String           | optional           |                    |
| [token.tokenization.defaulttag](#ttd)     | Boolean          | optional           | false              |
| [token.tokenization.sub](#tts)            | Boolean          | optional           | true               |
| [token.tokenize](#tt)                  	| Boolean          | optional           | false              |
| [token.tokenize.lang](#ttl)               | String           | optional           | en	             |

<a name="adr"></a>
### annotation.default.remove 

By default there is an annotation added to each node to indicate which element
is responsible for this node. This flag disables adding these annotations.

For example this
```xml
<p>In the beginning was the Word, and the Word was with God,</p><phr>and the Word was God.</phr>
```
would result in two nodes containing the text and the annotations, here marked with curly brackets:
```
[Node1]{p=p}: In the beginning was the Word, and the Word was with God,
[Node2]{phr=phr}: and the Word was God.
```

By enabling this property no such annotation would be created:

```
[Node1]: In the beginning was the Word, and the Word was with God,
[Node2]: and the Word was God.
```


<a name="aer"></a>
### annotation.element.rename

A [large number of annotations](#adr) in Salt comes from the element names existing in TEI.
To be able to differentiate, e.g. two hierarchical nodes coming first from \<p\> and a second
from \<phr\>,

```xml
<p>In the beginning was the Word, and the Word was with God,</p><phr>and the Word was God.</phr>
```


a generic annotation is used. The default is to use the element-name.
The annotation.element.rename flag allows for customizing the key of such an annotation.
The following format has to be met: 

```
annotation.element.rename = pb:PNAME;phr:Phrase
```

While the default would look like this:

```
[Node1]{p=p}: In the beginning was the Word, and the Word was with God,
[Node2]{phr=phr}: and the Word was God.
```

Using the examplary renamings results in:
```
[Node1]{PNAME=p}: In the beginning was the Word, and the Word was with God,
[Node2]{Phrase=phr}: and the Word was God.
```

<a name="an"></a>
### annotation.namespace

To differentiate annotations with the same name, it is possible to add the
namespace coming from TEI to annotations. Example:

```xml
<a attr="good"> text </a> <b attr="good"> text </b>
```

Here, enabling this flag would add the namespaces "a" and "b" to the "attr=good"
annotations:

```
a:attr=good
b:attr=good
```


<a name="ats"></a>
### annotation.token.span

Sometimes tokens are annotated directly. This flag enables adding these annotations
as spans (SSpan). By default, token annotations are not added as spans.

<a name="avr"></a>
### annotation.value.rename

The annotation.value.rename flag is very similar to [annotation.element.rename](#aer), beside here the annotation value can be customized. The format is:

The following format has to be met: 

```
annotation.element.rename = pb:PVALUE;phr:Phrase
```

While the default would look like this:

```
[Node1]{p=p}: In the beginning was the Word, and the Word was with God,
[Node2]{phr=phr}: and the Word was God.
```

Using the examplary renamings results in:
```
[Node1]{p=PVALUE}: In the beginning was the Word, and the Word was with God,
[Node2]{phr=Phrase}: and the Word was God.
```

<a name="eft"></a>
### element.foreign.token

By default this flag imports the text node inside of a \<foreign\> element as a token. By setting this property
to "false" you can make the TEIImporter ignore the element.

In case of "false" these examples will be treated identically:

```xml
<p>And seeing the <foreign xml:lang="la">multitudes</foreign>, he went up into a mountain:
 </p>
```

```xml
<p>And seeing the multitudes, he went up into a mountain:
 </p>
```

<a name="ega"></a>
### element.generic.attribute

By default, attributes to elements without [nongeneric](#elements) handling are ignored. To add
those attributes, enable this flag.

<a name="egn"></a>
### element.generic.node

By default elements without a [nongeneric](#elements) handling in the importer are added
as hierarchical nodes. You can also import them as spans (by setting the
property to "span") or ignore (by setting the property to "false") them.

```
element.generic.node = span
element.generic.node = false
```

Values different to "struct", "span" or "false" will make the importer ignore
elements without a nongeneric handling as well. The default value is "struct".

<a name="esr"></a>
### element.surplus.remove

This property determines the behaviour of the TEIImporter regarding the element [\<surplus\>](http://www.tei-c.org/release/doc/tei-p5-doc/de/html/ref-surplus.html).
If it is set to "true", the text node will be ignored. If it is set  to "false", the text node
will be imported as a token. The default value is "true".

<a name="eut"></a>
### element.unclear.token

By default this flag imports the text node inside of a \<unclear\> element as a token. By setting this property
to "false" you can make the TEIImporter ignore the element.

In case of "false" these examples will be treated identically:

```xml
<p>In the beginning was the Word, and the Word was with God,
and the <unclear reason="illegible">Word</unclear> was God.
 </p>
```

```xml
<p>In the beginning was the Word, and the Word was with God, and the Word was God.
 </p>
```


<a name="ml"></a>
### metadata.lastpartonly

Enabling this flag triggers the deletion of everything from metadata keys aside from the part
after the last '/'. The same goes for attributes, only the attribute name is kept.

For example, this
```
/fileDesc/publicationStmt/date
```
would become:
```
date
```

<a name="mrr"></a>
### metadata.redundant.remove

When handling metadata, the TEIImporter uses default mappings
and mappings set by the user. This flag decides whether more than one
SMetaAnnotation can contain the same information when metadata mappings are
used. If set to "false", redudant metadata will not be deleted. By default redundant metadata
are removed.

In case of a mapping like:
```
annotation.element.rename=/fileDesc/titleStmt/author:author
```
and TEI-XML like:
```xml
<TEI xmlns="http://www.tei-c.org/ns/1.0">
    <teiHeader>
        <fileDesc>
            <titleStmt>
                <author>Joseph Addison</author>
            </titleStmt>
        </fileDesc>
    </teiHeader>
    ...
```


By default only this metadate would be created:
```
author:Joseph Addison
```


If this property is set to "false", the following would be the result:
```
/fileDesc/titleStmt/author:Joseph Addison
author:Joseph Addison
```



<a name="mr1"></a>
### metadata.remove

This flag enables the mechanism to exclude certain metadata defined by the names in
metadata.remove.list .

<a name="mrl"></a>
### metadata.remove.list

Here you can define a list of names to be omitted. Names have to be separated by ";", e.g.:
```
metadata.remove.list = bibl;date;/fileDesc/publicationStmt/pubPlace
```

<a name="mr2"></a>
### metadata.rename

In addition to (or even replacing) the default metadata name mappings you can
set your own metadata name mappings with this flag. The following example
illustrates this:

```xml
<TEI xmlns="http://www.tei-c.org/ns/1.0">
    <teiHeader>
        <fileDesc>
            <sourceDesc>
                <pubPlace>Berlin</pubPlace>
            </sourceDesc>
        </fileDesc>
    </teiHeader>
    ...
```


```
metadata.rename = /fileDesc/sourceDesc/pubPlace:Place;/fileDesc/titleStmt/author:Author
```

this would lead to this created metadate:

```
Place = Berlin
```

<a name="ttd"></a>
### token.tokenization.defaulttag

The user declares that there is one and only one element responsible for
mapping tokens to Salt. Default is \<w\>. In this case [token.tokenization.sub](#tts) should be disabled, otherwise
unexpected behaviour may occur.

<a name="tts"></a>
### token.tokenization.sub

By default, the text nodes between elements will be imported as tokens everywhere.
This option should only be disabled if you can [guarantee that there is no text outside of the \<w\>-element](#ttd) or
if you can get over losing parts of the primary text.

<a name="tt"></a>
### token.tokenize

This option is useful, if your TEI document contains sections of text that are not tokenized.
By default, text will not be tokenized by the TEIImporter. Using the tokenizer will slow the processing by a considerable amount of time.

<a name="ttl"></a>
### token.tokenize.lang

The tokenizer currently supports four languages: English, German,
Italian, French. To choose a language, use the respective ISO 639-1
language code (en, de, it, fr). If no value or a non-supported value is
set, the tokenizer will default to English. The tokenizer produces good
results for other languages besides those four as well, the main problem
would be missing lists for abbreviations.

<a name="elements"></a>
## Elements with nongeneric Handling (beside metadata)
| element name       
|----------------------------------------|
| text      |
| body      |
| lb        |
| pb        |
| w         |
| phr       |
| head      |
| figure    |
| div       |
| p         |
| foreign   |
| m         |
| unclear   |
| surplus   |
| gap       |
| surplus   |

