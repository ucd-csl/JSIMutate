# JSIMutate: Understanding Performance Results through Mutations

## Abstract
Understanding the performance characteristics of software systems is particular relevant when looking at design alternatives. However, it is a very challenging problem, due to the complexity of interpreting the role and incidence of the different system elements on performance metrics of interest, such as system response time or resources utilisation. This work introduces JSIMutate, a tool that makes use of queueing network performance models and enables the analysis of mutations of a model reflecting possible design changes to support designers in identifying the model elements that most likely contribute to improving or worsening the system's performance.

## Structure of the repository
* Folder *code* contains the code of the tool
* JSIMutate is available as jar file [here](https://github.com/ucd-csl/JSIMutate/raw/main/code/JSIMutate.jar)
* Folder *example* contains an example of input file (the jsmi file and the json files) and the results obtained after the execution of JSIMutate.

## Usage
```
Usage: java -jar JSIMutate.jar [-help] [-saveMutant]
                               [-outFolder=<resultsFolder>]
                               [-timeout=<testTimeoutMin>]
                               [-operators=<operators>[,<operators>...]]...
                               <jsonPath>
      <jsonPath>    path of the json file describing the workloads to consider
      -help         display this help and exit
      -operators=<operators>[,<operators>...]
                    selected mutation operators: CQSize, CNServers, CQStrat
      -outFolder=<resultsFolder>
                    path of folder for experimental results
      -saveMutants  save the generated mutants
      -timeout=<testTimeoutMin>
                    timeout in minutes
```

## Screencast
A screencast of the tool is available [here](https://drive.google.com/file/d/1Gk2rh0DHRlJAj9TEhBV5jSUE-RiNhgjU/view?usp=sharing)

## People
* Thomas Laurent [https://laurenttho3.github.io/](https://laurenttho3.github.io/)
* Paolo Arcaini [https://group-mmm.org/~arcaini/](https://group-mmm.org/~arcaini/)
* Catia Trubiani [https://cs.gssi.it/catia.trubiani/](https://cs.gssi.it/catia.trubiani/)
* Anthony Ventresque [https://csl.ucd.ie/index.php/anthony-ventresque/](https://csl.ucd.ie/index.php/anthony-ventresque/)

## Paper
Thomas Laurent, Paolo Arcaini, Catia Trubiani, and Anthony Ventresque. JSIMutate: understanding performance results through mutations. In Proceedings of the 30th ACM Joint European Software Engineering Conference and Symposium on the Foundations of Software Engineering (ESEC/FSE 2022) [[doi](https://doi.org/10.1145/3540250.3558930)]
