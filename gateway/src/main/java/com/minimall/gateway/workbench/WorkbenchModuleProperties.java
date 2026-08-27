package com.minimall.gateway.workbench;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "mini-mall.workbench")
public class WorkbenchModuleProperties {

    private Map<String, ModuleSpec> modules = new LinkedHashMap<>();

    public Map<String, ModuleSpec> getModules() {
        return modules;
    }

    public void setModules(Map<String, ModuleSpec> modules) {
        this.modules = modules;
    }

    public static class ModuleSpec {
        private String path;
        private String microAppName;
        private String version = "dev";
        /** Dev HTML entry when urls is empty. */
        private String entry;
        private List<String> urls = new ArrayList<>();
        private List<RuntimeDep> runtime = new ArrayList<>();
        private List<String> scriptAttributes = List.of("crossorigin=anonymous");

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getMicroAppName() {
            return microAppName;
        }

        public void setMicroAppName(String microAppName) {
            this.microAppName = microAppName;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getEntry() {
            return entry;
        }

        public void setEntry(String entry) {
            this.entry = entry;
        }

        public List<String> getUrls() {
            return urls;
        }

        public void setUrls(List<String> urls) {
            this.urls = urls != null ? urls : new ArrayList<>();
        }

        public List<RuntimeDep> getRuntime() {
            return runtime;
        }

        public void setRuntime(List<RuntimeDep> runtime) {
            this.runtime = runtime != null ? runtime : new ArrayList<>();
        }

        public List<String> getScriptAttributes() {
            return scriptAttributes;
        }

        public void setScriptAttributes(List<String> scriptAttributes) {
            this.scriptAttributes = scriptAttributes;
        }
    }

    public static class RuntimeDep {
        private String name;
        private String version;
        private List<String> urls = new ArrayList<>();
        private String library;
        private String exposeName;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public List<String> getUrls() {
            return urls;
        }

        public void setUrls(List<String> urls) {
            this.urls = urls != null ? urls : new ArrayList<>();
        }

        public String getLibrary() {
            return library;
        }

        public void setLibrary(String library) {
            this.library = library;
        }

        public String getExposeName() {
            return exposeName;
        }

        public void setExposeName(String exposeName) {
            this.exposeName = exposeName;
        }
    }
}
