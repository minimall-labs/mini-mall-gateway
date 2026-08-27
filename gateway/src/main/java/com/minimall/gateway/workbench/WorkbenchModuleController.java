package com.minimall.gateway.workbench;

import com.minimall.common.api.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/workbench")
public class WorkbenchModuleController {

    private final WorkbenchModuleProperties properties;

    public WorkbenchModuleController(WorkbenchModuleProperties properties) {
        this.properties = properties;
    }

    /**
     * Qianniu-style microAppRoutes: path + runtime + urls (css/js).
     */
    @GetMapping("/micro-app-routes")
    public Mono<ApiResponse<List<MicroAppRouteView>>> microAppRoutes() {
        List<MicroAppRouteView> routes = new ArrayList<>();
        for (Map.Entry<String, WorkbenchModuleProperties.ModuleSpec> item : properties.getModules().entrySet()) {
            MicroAppRouteView route = toRouteView(item.getKey(), item.getValue());
            if (route != null) {
                routes.add(route);
            }
        }
        return Mono.just(ApiResponse.ok(routes));
    }

    /** Legacy slim manifest for qiankun entry only. */
    @GetMapping("/modules")
    public Mono<ApiResponse<List<WorkbenchModuleView>>> modules() {
        List<WorkbenchModuleView> views = new ArrayList<>();
        for (Map.Entry<String, WorkbenchModuleProperties.ModuleSpec> item : properties.getModules().entrySet()) {
            MicroAppRouteView route = toRouteView(item.getKey(), item.getValue());
            if (route == null) {
                continue;
            }
            String entry = route.entry();
            if (entry == null || entry.isBlank()) {
                if (!route.urls().isEmpty()) {
                    entry = route.urls().get(route.urls().size() - 1);
                }
            }
            if (entry == null || entry.isBlank()) {
                continue;
            }
            views.add(new WorkbenchModuleView(route.key(), route.appVersion(), entry));
        }
        return Mono.just(ApiResponse.ok(views));
    }

    private MicroAppRouteView toRouteView(String key, WorkbenchModuleProperties.ModuleSpec spec) {
        if (spec == null) {
            return null;
        }
        boolean hasUrls = spec.getUrls() != null && !spec.getUrls().isEmpty();
        boolean hasEntry = spec.getEntry() != null && !spec.getEntry().isBlank();
        if (!hasUrls && !hasEntry) {
            return null;
        }

        String path = spec.getPath() != null && !spec.getPath().isBlank() ? spec.getPath() : "/" + key;
        String microAppName =
                spec.getMicroAppName() != null && !spec.getMicroAppName().isBlank() ? spec.getMicroAppName() : key;

        List<RuntimeDepView> runtime = spec.getRuntime() == null || spec.getRuntime().isEmpty()
                ? WorkbenchRuntimeDefaults.sharedRuntime().stream().map(this::toRuntimeView).toList()
                : spec.getRuntime().stream().map(this::toRuntimeView).toList();

        return new MicroAppRouteView(
                key,
                microAppName,
                path,
                spec.getVersion(),
                runtime,
                spec.getUrls() != null ? spec.getUrls() : List.of(),
                hasEntry ? spec.getEntry() : "",
                spec.getScriptAttributes() != null ? spec.getScriptAttributes() : List.of());
    }

    private RuntimeDepView toRuntimeView(WorkbenchModuleProperties.RuntimeDep dep) {
        return new RuntimeDepView(
                dep.getName(),
                dep.getVersion(),
                dep.getUrls() != null ? dep.getUrls() : List.of(),
                dep.getLibrary(),
                dep.getExposeName());
    }

    public record MicroAppRouteView(
            String key,
            String microAppName,
            String path,
            String appVersion,
            List<RuntimeDepView> runtime,
            List<String> urls,
            String entry,
            List<String> scriptAttributes) {}

    public record RuntimeDepView(
            String name, String version, List<String> urls, String library, String exposeName) {}

    public record WorkbenchModuleView(String key, String version, String entry) {}
}
