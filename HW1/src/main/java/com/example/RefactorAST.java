package com.example;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.stmt.IfStmt;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class RefactorAST {
    public static void main(String[] args) throws IOException {
        String filePath = "src/main/resources/SimpleComparison.java"; // Hardcoded input file

        // Parse the Java file into an AST
        CompilationUnit cu = StaticJavaParser.parse(new File(filePath));

        // Perform refactoring: Change "!=" to "==" and swap "then" and "else" statements
        cu.findAll(IfStmt.class).forEach(ifStmt -> {
            if (ifStmt.hasElseBranch()) {
                ifStmt.getCondition().ifBinaryExpr(binaryExpr -> {
                    if (binaryExpr.getOperator() == BinaryExpr.Operator.NOT_EQUALS) {
                        // Change "!=" to "=="
                        binaryExpr.setOperator(BinaryExpr.Operator.EQUALS);
                        
                        // Swap "then" and "else" statements
                        ifStmt.setThenStmt(ifStmt.getElseStmt().get());
                        ifStmt.setElseStmt(ifStmt.getThenStmt());
                    }
                });
            }
        });

        // Save the refactored AST as YAML
        saveAstToYaml(cu, "refactored_ast.yaml");

        // Output the refactored code (for verification)
        System.out.println(cu.toString());
    }

    private static void saveAstToYaml(CompilationUnit cu, String outputPath) throws IOException {
        Yaml yaml = new Yaml();
        String yamlString = yaml.dump(cu.toString());
        Files.write(Paths.get(outputPath), yamlString.getBytes());
    }
}
