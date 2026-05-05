# Verifying Robotic Designs Using a UML-Based


## Table of contents
* [General info](#general-info)
* [The Core Contribution the Astah UML Plugin](#The-Core-Contribution-the-AstahUML-Plugin)
* [Prerequisites](#Prerequisites)
* [Tools](#tools)
* [Models downloads](#Models-downloads) 
* [Importing the plug-in source code](#importing-the-plug-in-source-code)
* [Download the RoboChart models](#download-the-robochart-models)
* [Importing the RoboChart models](#importing-the-robochart-models)
* [Download the Astah models](#download-the-astah-models)
* [Compilation and execution of the plugin in AstahUML](#compilation-and-execution-of-the-plugin-in-astahuml)
* [Verification of robotic properties](#verification-of-robotic-properties)



## General info
This project presents an extension for the formal verification of robotic controller models in RoboChart. The primary objective is to overcome the uncertainty of traditional simulations by providing mathematical guarantees that the robotic software will behave as expected, thereby avoiding critical failures. 

## The Core Contribution the Astah UML Plugin

* Property Specification: It implements a language based on UML activity diagrams to define time constraints and operation orderings.
* Automated Translation: The plugin for Astah UML tools that translates property diagrams into tock-CSP.
* Seamless Verification: It enables direct interaction with the FDR verification tool.
* Bidirectional Feedback: Translates verification results back into the visual environment, displaying them as Sequence Diagrams for intuitive debugging.


## Prerequisites

To reproduce or utilise this verification framework, the following tools are required:
* [Java8] (Adoptium/Temurin): The specific Java Runtime Environment (JRE) required to ensure compatibility with Astah and the plugin's execution.(https://adoptium.net/temurin/releases/?version=8&os=any&arch=any)  
* [Eclipse-IDE-2021-12R] Install Eclipse Modeling Tools, (https://www.eclipse.org/downloads/packages/release/2021-12/r).
* [Eclipse-IDE-2023-12R] Install Eclipse IDE for Java Developers, (https://www.eclipse.org/downloads/packages/release/2023-12/r/eclipse-ide-java-developers)

## Tools

* [RoboTool]: RoboTool enables graphical modelling, validation, and the automatic generation of formal methods to verify the properties of RoboChart models. Download, installation, and configuration details are available on the RoboStar website (https://robostar.cs.york.ac.uk/robotool/).
* [AstahUML]: Astah UML modelling environment, download in (https://astah.net/products/astah-uml/).
* [FDR4] (Failures-Divergences Refinement): The underlying formal verification engine used to analyse CSP models, (https://cocotec.io/fdr/)


## Models downloads

* [RoboChart] Download (https://github.com/lucasalbertins/refinement/tree/activityDiagramPropertyTime/RoboChart%20model%20Timed)
* [Astah] Download (https://github.com/lucasalbertins/refinement/tree/activityDiagramPropertyTime/Astah%20Model%20Timed)


## Importing the plug-in source code 

* [Download] Download the refinement-activityDiagramPropertyTime.zip in (https://github.com/lucasalbertins/refinement/tree/activityDiagramPropertyTime)

* In Eclipse Developers import the project File → Import → Existing Projects into Workspace Select the extracted folder

## Download the RoboChart models

* Download the models from the RoboChart Model Timed folder.

## Importing the RoboChart models

*  Import the RoboChart models into Eclipse Modelling. Details available on the RoboStar website  (https://robostar.cs.york.ac.uk/robotool/).

## Download the Astah models

*  Download the models from the Astah Model Timed folder.

## Compilation and execution of the plugin in AstahUML.

* Using the terminal, compile the AstahUML plugin source code with the command "C:\Users\name\git\refinement\ref>astah-build". Then start Astah by executing "C:\Users\name\git\refinement\ref>astah-launch".

## Verification of robotic properties

* Within Astah-UML, open the models and follow the approach documented in the paper entitled "Verifying Robotic Designs Using a UML-Based Language for Time-Constrained Properties".



