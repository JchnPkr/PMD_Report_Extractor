# PMD_Report_Extractor
Extracts rules from a given PMD report for a given rule and creates or updates a given exclude file.

## What it is good for
Sometimes there are multiple errors or warnings for a rule you want to ignore anways, concering numerous files.
With this commandline tool you can give paths to the report and exclude files and a rule you want to be excluded
as arguments and a new or updated exclude file will be created. An update will sort the excludes by filepath.

This will save you time and nerves extracting the filepaths yourself and adding them manually to the excludes.

## Usage
java -jar PMD_Rule_Extractor.jar some/path/toResourceFile.xml another/path/toExcludeFile.xml RuleNameToAddToExclude
