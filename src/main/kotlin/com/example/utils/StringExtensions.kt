package com.example.utils

import java.io.File

//fun isProductionServer(): Boolean = SecretVariablesService.get(SecretVariableName.ProductionServer, "false").toBoolean()
fun getUserWorkingDirectory(isProductionServer: Boolean): String {
    if (!isProductionServer) {
        return File(".").canonicalPath
    }
    return File(object {}.javaClass.protectionDomain.codeSource.location.toURI().path).parent
}