package es.gobcan.istac.edatos.external.users.util;

import java.util.*;

import es.gobcan.istac.edatos.external.users.core.domain.CategoryEntity;
import es.gobcan.istac.edatos.external.users.core.domain.vo.InternationalStringVO;

public class CategoryEntityBuilder {
    private Long id;
    private String code;
    private InternationalStringVO name;
    private Integer index;
    private CategoryEntity parent;
    private final Set<CategoryEntity> children = new HashSet<>();

    public static CategoryEntityBuilder categoryBuilder() {
        return new CategoryEntityBuilder();
    }

    public CategoryEntityBuilder setId(Long id) {
        this.id = id;
        return this;
    }

    public CategoryEntityBuilder setCode(String code) {
        this.code = code;
        return this;
    }

    public CategoryEntityBuilder setName(InternationalStringVO name) {
        this.name = name;
        return this;
    }

    public CategoryEntityBuilder setName(String locale, String label) {
        this.name = InternationalStringUtils.createIntStr(locale, label);
        return this;
    }

    public CategoryEntityBuilder setName(String label) {
        return setName("es", label);
    }

    public CategoryEntityBuilder addChild(CategoryEntity child) {
        this.children.add(child);
        return this;
    }

    public CategoryEntityBuilder addChildren(CategoryEntity... children) {
        this.children.addAll(Arrays.asList(children));
        return this;
    }

    public CategoryEntityBuilder setChildren(List<CategoryEntity> children) {
        this.children.clear();
        this.children.addAll(children);
        return this;
    }

    public CategoryEntityBuilder setIndex(Integer index) {
        this.index = index;
        return this;
    }

    public CategoryEntityBuilder setParent(CategoryEntity parent) {
        this.parent = parent;
        return this;
    }

    public CategoryEntity build() {
        CategoryEntity category = new CategoryEntity();
        category.setId(id);
        category.setCode(code == null ? UUID.randomUUID().toString() : code);
        category.setName(name);
        category.setIndex(index == null ? 1 : index);
        category.setParent(parent);
        category.setChildren(children);
        return category;
    }
}
