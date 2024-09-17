package com.example.project1.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Service
public class verilogService {

    public String processFile(MultipartFile design, MultipartFile tb) {
        try {
            File savedDesignFile = saveFile(design);
            File savedTbFile = saveFile(tb);

            // Log file paths for debugging
            System.out.println("Saved Design File: " + savedDesignFile.getAbsolutePath());
            System.out.println("Saved TB File: " + savedTbFile.getAbsolutePath());


            // Command construction (ensure filenames are sanitized)
            String cmd = String.format("iverilog -o output %s %s",
                    escapeShellArg(savedDesignFile.getAbsolutePath()),
                    escapeShellArg(savedTbFile.getAbsolutePath()));

            // Execute the compile command
            Process compileProcess = Runtime.getRuntime().exec(cmd);
            compileProcess.waitFor();

            // Check for compile errors
            String compileErrors = readInputStream(compileProcess.getErrorStream());
            if (compileProcess.exitValue() != 0 || !compileErrors.isEmpty()) {
                return "Compile Error: " + compileErrors;
            }

            // Execute the simulation command
            Process simulationProcess = Runtime.getRuntime().exec("vvp output");
            simulationProcess.waitFor();

            // Return simulation results
            return readInputStream(simulationProcess.getInputStream());

        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    private File saveFile(MultipartFile tempFile) {
        try {
            File file = new File(System.getProperty("java.io.tmpdir"),
                    System.currentTimeMillis() + "_" + tempFile.getOriginalFilename());
            tempFile.transferTo(file);
            return file;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file", e);
        }
    }

    private String escapeShellArg(String arg) {
        // Simple escape mechanism to prevent injection attacks
        return "\"" + arg.replace("\"", "\\\"") + "\"";
    }

    private String readInputStream(InputStream inputStream) throws IOException {
        try (InputStream is = inputStream) {
            return new String(is.readAllBytes());
        }
    }
}
