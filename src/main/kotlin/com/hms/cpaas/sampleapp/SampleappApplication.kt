package com.hms.cpaas.sampleapp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SampleappApplication

fun main(args: Array<String>) {
    runApplication<SampleappApplication>(*args)
}
