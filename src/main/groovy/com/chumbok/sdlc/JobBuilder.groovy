#!groovy
package com.chumbok.sdlc

@Grab('org.yaml:snakeyaml:1.17')
import org.yaml.snakeyaml.Yaml

def yaml = readYaml file:"job-definition.yaml"
println yaml


