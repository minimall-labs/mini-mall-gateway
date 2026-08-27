package com.minimall.gateway.workbench;

import java.util.ArrayList;
import java.util.List;

final class WorkbenchRuntimeDefaults {

    static List<WorkbenchModuleProperties.RuntimeDep> sharedRuntime() {
        List<WorkbenchModuleProperties.RuntimeDep> list = new ArrayList<>();
        list.add(dep(
                "dayjs",
                "1.11.10",
                "dayjs",
                "dayjs@1.11.10",
                "https://g.alicdn.com/code/lib/dayjs/1.11.10/dayjs.min.js"));
        list.add(dep(
                "react",
                "18.3.1",
                "React",
                "React@18.3.1",
                "https://g.alicdn.com/code/lib/react/18.3.1/umd/react.production.min.js"));
        list.add(dep(
                "react-dom",
                "18.3.1",
                "ReactDOM",
                "ReactDOM@18.3.1",
                "https://g.alicdn.com/code/lib/react-dom/18.3.1/umd/react-dom.production.min.js"));
        list.add(dep(
                "antd",
                "5.24.2",
                "antd",
                "antd@5.24.2",
                "https://g.alicdn.com/code/lib/antd/5.24.2/antd.min.js"));
        return list;
    }

    private static WorkbenchModuleProperties.RuntimeDep dep(
            String name,
            String version,
            String library,
            String exposeName,
            String url) {
        WorkbenchModuleProperties.RuntimeDep dep = new WorkbenchModuleProperties.RuntimeDep();
        dep.setName(name);
        dep.setVersion(version);
        dep.setLibrary(library);
        dep.setExposeName(exposeName);
        dep.setUrls(List.of(url));
        return dep;
    }
}
