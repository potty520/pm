import { computed, onMounted, ref } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { createAdminCategory, createAdminSession, deleteAdminCategory, deleteAdminSession, getAdminCategories, getAdminLogs, getAdminSessions, updateAdminCategory, updateAdminSession } from "@/api/admin";
import { bindPermission, bindRole, createPermission, createRole, getPermissions, getRoles } from "@/api/system";
const activeTab = ref("category");
const categories = ref([]);
const sessions = ref([]);
const logs = ref([]);
const roles = ref([]);
const permissions = ref([]);
const categoryLoading = ref(false);
const sessionLoading = ref(false);
const logLoading = ref(false);
const logPage = ref(1);
const logPageSize = ref(10);
const categoryForm = ref({ name: "", sort: 0 });
const sessionForm = ref({ name: "", description: "" });
const roleForm = ref({ roleCode: "", roleName: "", status: 1 });
const permForm = ref({ permCode: "", permName: "" });
const bindRoleForm = ref({ userId: 2, roleId: undefined });
const bindPermForm = ref({ roleId: undefined, permissionId: undefined });
const pagedLogs = computed(() => {
    const start = (logPage.value - 1) * logPageSize.value;
    return logs.value.slice(start, start + logPageSize.value);
});
const loadCategories = async () => {
    categoryLoading.value = true;
    try {
        const res = await getAdminCategories();
        categories.value = res.data || [];
    }
    finally {
        categoryLoading.value = false;
    }
};
const loadSessions = async () => {
    sessionLoading.value = true;
    try {
        const res = await getAdminSessions();
        sessions.value = res.data || [];
    }
    finally {
        sessionLoading.value = false;
    }
};
const loadLogs = async () => {
    logLoading.value = true;
    try {
        const res = await getAdminLogs(200);
        logs.value = res.data || [];
        if (logPage.value !== 1) {
            logPage.value = 1;
        }
    }
    finally {
        logLoading.value = false;
    }
};
const loadAcl = async () => {
    const [roleRes, permRes] = await Promise.all([getRoles(), getPermissions()]);
    roles.value = roleRes.data || [];
    permissions.value = permRes.data || [];
};
const handleCreateCategory = async () => {
    if (!categoryForm.value.name)
        return ElMessage.warning("请输入分类名称");
    try {
        await createAdminCategory({ ...categoryForm.value, status: 1, parentId: 0 });
        categoryForm.value.name = "";
        await loadCategories();
        ElMessage.success("新增分类成功");
    }
    catch (error) {
        ElMessage.error("新增分类失败");
    }
};
const handleDeleteCategory = async (id) => {
    try {
        await deleteAdminCategory(id);
        await loadCategories();
        ElMessage.success("删除分类成功");
    }
    catch (error) {
        ElMessage.error("删除分类失败");
    }
};
const handleEditCategory = async (row) => {
    try {
        const { value: name } = await ElMessageBox.prompt("请输入新分类名称", "编辑分类", {
            inputValue: row.name
        });
        await updateAdminCategory({ ...row, name });
        await loadCategories();
        ElMessage.success("更新分类成功");
    }
    catch (error) {
        // cancel or failed request
    }
};
const handleCreateSession = async () => {
    if (!sessionForm.value.name)
        return ElMessage.warning("请输入专场名称");
    try {
        await createAdminSession({ ...sessionForm.value, status: 1 });
        sessionForm.value.name = "";
        sessionForm.value.description = "";
        await loadSessions();
        ElMessage.success("新增专场成功");
    }
    catch (error) {
        ElMessage.error("新增专场失败");
    }
};
const handleDeleteSession = async (id) => {
    try {
        await deleteAdminSession(id);
        await loadSessions();
        ElMessage.success("删除专场成功");
    }
    catch (error) {
        ElMessage.error("删除专场失败");
    }
};
const handleEditSession = async (row) => {
    try {
        const { value: name } = await ElMessageBox.prompt("请输入新专场名称", "编辑专场", {
            inputValue: row.name
        });
        await updateAdminSession({ ...row, name });
        await loadSessions();
        ElMessage.success("更新专场成功");
    }
    catch (error) {
        // cancel or failed request
    }
};
const handleCreateRole = async () => {
    if (!roleForm.value.roleCode || !roleForm.value.roleName)
        return ElMessage.warning("请填写角色信息");
    try {
        await createRole(roleForm.value);
        roleForm.value.roleCode = "";
        roleForm.value.roleName = "";
        await loadAcl();
        ElMessage.success("新增角色成功");
    }
    catch (error) {
        ElMessage.error("新增角色失败");
    }
};
const handleCreatePerm = async () => {
    if (!permForm.value.permCode || !permForm.value.permName)
        return ElMessage.warning("请填写权限信息");
    try {
        await createPermission(permForm.value);
        permForm.value.permCode = "";
        permForm.value.permName = "";
        await loadAcl();
        ElMessage.success("新增权限成功");
    }
    catch (error) {
        ElMessage.error("新增权限失败");
    }
};
const handleBindRole = async () => {
    if (!bindRoleForm.value.roleId)
        return ElMessage.warning("请选择角色");
    try {
        await bindRole(bindRoleForm.value);
        ElMessage.success("绑定角色成功");
    }
    catch (error) {
        ElMessage.error("绑定角色失败");
    }
};
const handleBindPerm = async () => {
    if (!bindPermForm.value.roleId || !bindPermForm.value.permissionId)
        return ElMessage.warning("请选择角色与权限");
    try {
        await bindPermission(bindPermForm.value);
        ElMessage.success("绑定权限成功");
    }
    catch (error) {
        ElMessage.error("绑定权限失败");
    }
};
onMounted(async () => {
    try {
        await Promise.all([loadCategories(), loadSessions(), loadAcl(), loadLogs()]);
    }
    catch (error) {
        ElMessage.error("管理页数据加载失败，请确认使用管理员token");
    }
});
debugger; /* PartiallyEnd: #3632/scriptSetup.vue */
const __VLS_ctx = {};
let __VLS_components;
let __VLS_directives;
__VLS_asFunctionalElement(__VLS_intrinsicElements.div, __VLS_intrinsicElements.div)({
    ...{ style: {} },
});
__VLS_asFunctionalElement(__VLS_intrinsicElements.h2, __VLS_intrinsicElements.h2)({});
const __VLS_0 = {}.ElTabs;
/** @type {[typeof __VLS_components.ElTabs, typeof __VLS_components.elTabs, typeof __VLS_components.ElTabs, typeof __VLS_components.elTabs, ]} */ ;
// @ts-ignore
const __VLS_1 = __VLS_asFunctionalComponent(__VLS_0, new __VLS_0({
    modelValue: (__VLS_ctx.activeTab),
}));
const __VLS_2 = __VLS_1({
    modelValue: (__VLS_ctx.activeTab),
}, ...__VLS_functionalComponentArgsRest(__VLS_1));
__VLS_3.slots.default;
const __VLS_4 = {}.ElTabPane;
/** @type {[typeof __VLS_components.ElTabPane, typeof __VLS_components.elTabPane, typeof __VLS_components.ElTabPane, typeof __VLS_components.elTabPane, ]} */ ;
// @ts-ignore
const __VLS_5 = __VLS_asFunctionalComponent(__VLS_4, new __VLS_4({
    label: "分类管理",
    name: "category",
}));
const __VLS_6 = __VLS_5({
    label: "分类管理",
    name: "category",
}, ...__VLS_functionalComponentArgsRest(__VLS_5));
__VLS_7.slots.default;
__VLS_asFunctionalElement(__VLS_intrinsicElements.div, __VLS_intrinsicElements.div)({
    ...{ style: {} },
});
const __VLS_8 = {}.ElInput;
/** @type {[typeof __VLS_components.ElInput, typeof __VLS_components.elInput, ]} */ ;
// @ts-ignore
const __VLS_9 = __VLS_asFunctionalComponent(__VLS_8, new __VLS_8({
    modelValue: (__VLS_ctx.categoryForm.name),
    placeholder: "分类名称",
    ...{ style: {} },
}));
const __VLS_10 = __VLS_9({
    modelValue: (__VLS_ctx.categoryForm.name),
    placeholder: "分类名称",
    ...{ style: {} },
}, ...__VLS_functionalComponentArgsRest(__VLS_9));
const __VLS_12 = {}.ElInputNumber;
/** @type {[typeof __VLS_components.ElInputNumber, typeof __VLS_components.elInputNumber, ]} */ ;
// @ts-ignore
const __VLS_13 = __VLS_asFunctionalComponent(__VLS_12, new __VLS_12({
    modelValue: (__VLS_ctx.categoryForm.sort),
    min: (0),
}));
const __VLS_14 = __VLS_13({
    modelValue: (__VLS_ctx.categoryForm.sort),
    min: (0),
}, ...__VLS_functionalComponentArgsRest(__VLS_13));
const __VLS_16 = {}.ElButton;
/** @type {[typeof __VLS_components.ElButton, typeof __VLS_components.elButton, typeof __VLS_components.ElButton, typeof __VLS_components.elButton, ]} */ ;
// @ts-ignore
const __VLS_17 = __VLS_asFunctionalComponent(__VLS_16, new __VLS_16({
    ...{ 'onClick': {} },
    type: "primary",
}));
const __VLS_18 = __VLS_17({
    ...{ 'onClick': {} },
    type: "primary",
}, ...__VLS_functionalComponentArgsRest(__VLS_17));
let __VLS_20;
let __VLS_21;
let __VLS_22;
const __VLS_23 = {
    onClick: (__VLS_ctx.handleCreateCategory)
};
__VLS_19.slots.default;
var __VLS_19;
const __VLS_24 = {}.ElTable;
/** @type {[typeof __VLS_components.ElTable, typeof __VLS_components.elTable, typeof __VLS_components.ElTable, typeof __VLS_components.elTable, ]} */ ;
// @ts-ignore
const __VLS_25 = __VLS_asFunctionalComponent(__VLS_24, new __VLS_24({
    data: (__VLS_ctx.categories),
}));
const __VLS_26 = __VLS_25({
    data: (__VLS_ctx.categories),
}, ...__VLS_functionalComponentArgsRest(__VLS_25));
__VLS_asFunctionalDirective(__VLS_directives.vLoading)(null, { ...__VLS_directiveBindingRestFields, value: (__VLS_ctx.categoryLoading) }, null, null);
__VLS_27.slots.default;
const __VLS_28 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
const __VLS_29 = __VLS_asFunctionalComponent(__VLS_28, new __VLS_28({
    prop: "id",
    label: "ID",
    width: "80",
}));
const __VLS_30 = __VLS_29({
    prop: "id",
    label: "ID",
    width: "80",
}, ...__VLS_functionalComponentArgsRest(__VLS_29));
const __VLS_32 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
const __VLS_33 = __VLS_asFunctionalComponent(__VLS_32, new __VLS_32({
    prop: "name",
    label: "名称",
}));
const __VLS_34 = __VLS_33({
    prop: "name",
    label: "名称",
}, ...__VLS_functionalComponentArgsRest(__VLS_33));
const __VLS_36 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
const __VLS_37 = __VLS_asFunctionalComponent(__VLS_36, new __VLS_36({
    prop: "sort",
    label: "排序",
    width: "100",
}));
const __VLS_38 = __VLS_37({
    prop: "sort",
    label: "排序",
    width: "100",
}, ...__VLS_functionalComponentArgsRest(__VLS_37));
const __VLS_40 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
const __VLS_41 = __VLS_asFunctionalComponent(__VLS_40, new __VLS_40({
    label: "操作",
    width: "180",
}));
const __VLS_42 = __VLS_41({
    label: "操作",
    width: "180",
}, ...__VLS_functionalComponentArgsRest(__VLS_41));
__VLS_43.slots.default;
{
    const { default: __VLS_thisSlot } = __VLS_43.slots;
    const [{ row }] = __VLS_getSlotParams(__VLS_thisSlot);
    const __VLS_44 = {}.ElButton;
    /** @type {[typeof __VLS_components.ElButton, typeof __VLS_components.elButton, typeof __VLS_components.ElButton, typeof __VLS_components.elButton, ]} */ ;
    // @ts-ignore
    const __VLS_45 = __VLS_asFunctionalComponent(__VLS_44, new __VLS_44({
        ...{ 'onClick': {} },
        link: true,
    }));
    const __VLS_46 = __VLS_45({
        ...{ 'onClick': {} },
        link: true,
    }, ...__VLS_functionalComponentArgsRest(__VLS_45));
    let __VLS_48;
    let __VLS_49;
    let __VLS_50;
    const __VLS_51 = {
        onClick: (...[$event]) => {
            __VLS_ctx.handleEditCategory(row);
        }
    };
    __VLS_47.slots.default;
    var __VLS_47;
    const __VLS_52 = {}.ElButton;
    /** @type {[typeof __VLS_components.ElButton, typeof __VLS_components.elButton, typeof __VLS_components.ElButton, typeof __VLS_components.elButton, ]} */ ;
    // @ts-ignore
    const __VLS_53 = __VLS_asFunctionalComponent(__VLS_52, new __VLS_52({
        ...{ 'onClick': {} },
        type: "danger",
        link: true,
    }));
    const __VLS_54 = __VLS_53({
        ...{ 'onClick': {} },
        type: "danger",
        link: true,
    }, ...__VLS_functionalComponentArgsRest(__VLS_53));
    let __VLS_56;
    let __VLS_57;
    let __VLS_58;
    const __VLS_59 = {
        onClick: (...[$event]) => {
            __VLS_ctx.handleDeleteCategory(row.id);
        }
    };
    __VLS_55.slots.default;
    var __VLS_55;
}
var __VLS_43;
var __VLS_27;
var __VLS_7;
const __VLS_60 = {}.ElTabPane;
/** @type {[typeof __VLS_components.ElTabPane, typeof __VLS_components.elTabPane, typeof __VLS_components.ElTabPane, typeof __VLS_components.elTabPane, ]} */ ;
// @ts-ignore
const __VLS_61 = __VLS_asFunctionalComponent(__VLS_60, new __VLS_60({
    label: "专场管理",
    name: "session",
}));
const __VLS_62 = __VLS_61({
    label: "专场管理",
    name: "session",
}, ...__VLS_functionalComponentArgsRest(__VLS_61));
__VLS_63.slots.default;
__VLS_asFunctionalElement(__VLS_intrinsicElements.div, __VLS_intrinsicElements.div)({
    ...{ style: {} },
});
const __VLS_64 = {}.ElInput;
/** @type {[typeof __VLS_components.ElInput, typeof __VLS_components.elInput, ]} */ ;
// @ts-ignore
const __VLS_65 = __VLS_asFunctionalComponent(__VLS_64, new __VLS_64({
    modelValue: (__VLS_ctx.sessionForm.name),
    placeholder: "专场名称",
    ...{ style: {} },
}));
const __VLS_66 = __VLS_65({
    modelValue: (__VLS_ctx.sessionForm.name),
    placeholder: "专场名称",
    ...{ style: {} },
}, ...__VLS_functionalComponentArgsRest(__VLS_65));
const __VLS_68 = {}.ElInput;
/** @type {[typeof __VLS_components.ElInput, typeof __VLS_components.elInput, ]} */ ;
// @ts-ignore
const __VLS_69 = __VLS_asFunctionalComponent(__VLS_68, new __VLS_68({
    modelValue: (__VLS_ctx.sessionForm.description),
    placeholder: "描述",
    ...{ style: {} },
}));
const __VLS_70 = __VLS_69({
    modelValue: (__VLS_ctx.sessionForm.description),
    placeholder: "描述",
    ...{ style: {} },
}, ...__VLS_functionalComponentArgsRest(__VLS_69));
const __VLS_72 = {}.ElButton;
/** @type {[typeof __VLS_components.ElButton, typeof __VLS_components.elButton, typeof __VLS_components.ElButton, typeof __VLS_components.elButton, ]} */ ;
// @ts-ignore
const __VLS_73 = __VLS_asFunctionalComponent(__VLS_72, new __VLS_72({
    ...{ 'onClick': {} },
    type: "primary",
}));
const __VLS_74 = __VLS_73({
    ...{ 'onClick': {} },
    type: "primary",
}, ...__VLS_functionalComponentArgsRest(__VLS_73));
let __VLS_76;
let __VLS_77;
let __VLS_78;
const __VLS_79 = {
    onClick: (__VLS_ctx.handleCreateSession)
};
__VLS_75.slots.default;
var __VLS_75;
const __VLS_80 = {}.ElTable;
/** @type {[typeof __VLS_components.ElTable, typeof __VLS_components.elTable, typeof __VLS_components.ElTable, typeof __VLS_components.elTable, ]} */ ;
// @ts-ignore
const __VLS_81 = __VLS_asFunctionalComponent(__VLS_80, new __VLS_80({
    data: (__VLS_ctx.sessions),
}));
const __VLS_82 = __VLS_81({
    data: (__VLS_ctx.sessions),
}, ...__VLS_functionalComponentArgsRest(__VLS_81));
__VLS_asFunctionalDirective(__VLS_directives.vLoading)(null, { ...__VLS_directiveBindingRestFields, value: (__VLS_ctx.sessionLoading) }, null, null);
__VLS_83.slots.default;
const __VLS_84 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
const __VLS_85 = __VLS_asFunctionalComponent(__VLS_84, new __VLS_84({
    prop: "id",
    label: "ID",
    width: "80",
}));
const __VLS_86 = __VLS_85({
    prop: "id",
    label: "ID",
    width: "80",
}, ...__VLS_functionalComponentArgsRest(__VLS_85));
const __VLS_88 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
const __VLS_89 = __VLS_asFunctionalComponent(__VLS_88, new __VLS_88({
    prop: "name",
    label: "名称",
}));
const __VLS_90 = __VLS_89({
    prop: "name",
    label: "名称",
}, ...__VLS_functionalComponentArgsRest(__VLS_89));
const __VLS_92 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
const __VLS_93 = __VLS_asFunctionalComponent(__VLS_92, new __VLS_92({
    prop: "description",
    label: "描述",
}));
const __VLS_94 = __VLS_93({
    prop: "description",
    label: "描述",
}, ...__VLS_functionalComponentArgsRest(__VLS_93));
const __VLS_96 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
const __VLS_97 = __VLS_asFunctionalComponent(__VLS_96, new __VLS_96({
    label: "操作",
    width: "180",
}));
const __VLS_98 = __VLS_97({
    label: "操作",
    width: "180",
}, ...__VLS_functionalComponentArgsRest(__VLS_97));
__VLS_99.slots.default;
{
    const { default: __VLS_thisSlot } = __VLS_99.slots;
    const [{ row }] = __VLS_getSlotParams(__VLS_thisSlot);
    const __VLS_100 = {}.ElButton;
    /** @type {[typeof __VLS_components.ElButton, typeof __VLS_components.elButton, typeof __VLS_components.ElButton, typeof __VLS_components.elButton, ]} */ ;
    // @ts-ignore
    const __VLS_101 = __VLS_asFunctionalComponent(__VLS_100, new __VLS_100({
        ...{ 'onClick': {} },
        link: true,
    }));
    const __VLS_102 = __VLS_101({
        ...{ 'onClick': {} },
        link: true,
    }, ...__VLS_functionalComponentArgsRest(__VLS_101));
    let __VLS_104;
    let __VLS_105;
    let __VLS_106;
    const __VLS_107 = {
        onClick: (...[$event]) => {
            __VLS_ctx.handleEditSession(row);
        }
    };
    __VLS_103.slots.default;
    var __VLS_103;
    const __VLS_108 = {}.ElButton;
    /** @type {[typeof __VLS_components.ElButton, typeof __VLS_components.elButton, typeof __VLS_components.ElButton, typeof __VLS_components.elButton, ]} */ ;
    // @ts-ignore
    const __VLS_109 = __VLS_asFunctionalComponent(__VLS_108, new __VLS_108({
        ...{ 'onClick': {} },
        type: "danger",
        link: true,
    }));
    const __VLS_110 = __VLS_109({
        ...{ 'onClick': {} },
        type: "danger",
        link: true,
    }, ...__VLS_functionalComponentArgsRest(__VLS_109));
    let __VLS_112;
    let __VLS_113;
    let __VLS_114;
    const __VLS_115 = {
        onClick: (...[$event]) => {
            __VLS_ctx.handleDeleteSession(row.id);
        }
    };
    __VLS_111.slots.default;
    var __VLS_111;
}
var __VLS_99;
var __VLS_83;
var __VLS_63;
const __VLS_116 = {}.ElTabPane;
/** @type {[typeof __VLS_components.ElTabPane, typeof __VLS_components.elTabPane, typeof __VLS_components.ElTabPane, typeof __VLS_components.elTabPane, ]} */ ;
// @ts-ignore
const __VLS_117 = __VLS_asFunctionalComponent(__VLS_116, new __VLS_116({
    label: "权限管理",
    name: "acl",
}));
const __VLS_118 = __VLS_117({
    label: "权限管理",
    name: "acl",
}, ...__VLS_functionalComponentArgsRest(__VLS_117));
__VLS_119.slots.default;
__VLS_asFunctionalElement(__VLS_intrinsicElements.div, __VLS_intrinsicElements.div)({
    ...{ style: {} },
});
const __VLS_120 = {}.ElInput;
/** @type {[typeof __VLS_components.ElInput, typeof __VLS_components.elInput, ]} */ ;
// @ts-ignore
const __VLS_121 = __VLS_asFunctionalComponent(__VLS_120, new __VLS_120({
    modelValue: (__VLS_ctx.roleForm.roleCode),
    placeholder: "角色编码",
    ...{ style: {} },
}));
const __VLS_122 = __VLS_121({
    modelValue: (__VLS_ctx.roleForm.roleCode),
    placeholder: "角色编码",
    ...{ style: {} },
}, ...__VLS_functionalComponentArgsRest(__VLS_121));
const __VLS_124 = {}.ElInput;
/** @type {[typeof __VLS_components.ElInput, typeof __VLS_components.elInput, ]} */ ;
// @ts-ignore
const __VLS_125 = __VLS_asFunctionalComponent(__VLS_124, new __VLS_124({
    modelValue: (__VLS_ctx.roleForm.roleName),
    placeholder: "角色名称",
    ...{ style: {} },
}));
const __VLS_126 = __VLS_125({
    modelValue: (__VLS_ctx.roleForm.roleName),
    placeholder: "角色名称",
    ...{ style: {} },
}, ...__VLS_functionalComponentArgsRest(__VLS_125));
const __VLS_128 = {}.ElButton;
/** @type {[typeof __VLS_components.ElButton, typeof __VLS_components.elButton, typeof __VLS_components.ElButton, typeof __VLS_components.elButton, ]} */ ;
// @ts-ignore
const __VLS_129 = __VLS_asFunctionalComponent(__VLS_128, new __VLS_128({
    ...{ 'onClick': {} },
    type: "primary",
}));
const __VLS_130 = __VLS_129({
    ...{ 'onClick': {} },
    type: "primary",
}, ...__VLS_functionalComponentArgsRest(__VLS_129));
let __VLS_132;
let __VLS_133;
let __VLS_134;
const __VLS_135 = {
    onClick: (__VLS_ctx.handleCreateRole)
};
__VLS_131.slots.default;
var __VLS_131;
__VLS_asFunctionalElement(__VLS_intrinsicElements.div, __VLS_intrinsicElements.div)({
    ...{ style: {} },
});
const __VLS_136 = {}.ElInput;
/** @type {[typeof __VLS_components.ElInput, typeof __VLS_components.elInput, ]} */ ;
// @ts-ignore
const __VLS_137 = __VLS_asFunctionalComponent(__VLS_136, new __VLS_136({
    modelValue: (__VLS_ctx.permForm.permCode),
    placeholder: "权限编码",
    ...{ style: {} },
}));
const __VLS_138 = __VLS_137({
    modelValue: (__VLS_ctx.permForm.permCode),
    placeholder: "权限编码",
    ...{ style: {} },
}, ...__VLS_functionalComponentArgsRest(__VLS_137));
const __VLS_140 = {}.ElInput;
/** @type {[typeof __VLS_components.ElInput, typeof __VLS_components.elInput, ]} */ ;
// @ts-ignore
const __VLS_141 = __VLS_asFunctionalComponent(__VLS_140, new __VLS_140({
    modelValue: (__VLS_ctx.permForm.permName),
    placeholder: "权限名称",
    ...{ style: {} },
}));
const __VLS_142 = __VLS_141({
    modelValue: (__VLS_ctx.permForm.permName),
    placeholder: "权限名称",
    ...{ style: {} },
}, ...__VLS_functionalComponentArgsRest(__VLS_141));
const __VLS_144 = {}.ElButton;
/** @type {[typeof __VLS_components.ElButton, typeof __VLS_components.elButton, typeof __VLS_components.ElButton, typeof __VLS_components.elButton, ]} */ ;
// @ts-ignore
const __VLS_145 = __VLS_asFunctionalComponent(__VLS_144, new __VLS_144({
    ...{ 'onClick': {} },
    type: "primary",
}));
const __VLS_146 = __VLS_145({
    ...{ 'onClick': {} },
    type: "primary",
}, ...__VLS_functionalComponentArgsRest(__VLS_145));
let __VLS_148;
let __VLS_149;
let __VLS_150;
const __VLS_151 = {
    onClick: (__VLS_ctx.handleCreatePerm)
};
__VLS_147.slots.default;
var __VLS_147;
__VLS_asFunctionalElement(__VLS_intrinsicElements.div, __VLS_intrinsicElements.div)({
    ...{ style: {} },
});
const __VLS_152 = {}.ElInputNumber;
/** @type {[typeof __VLS_components.ElInputNumber, typeof __VLS_components.elInputNumber, ]} */ ;
// @ts-ignore
const __VLS_153 = __VLS_asFunctionalComponent(__VLS_152, new __VLS_152({
    modelValue: (__VLS_ctx.bindRoleForm.userId),
    min: (1),
}));
const __VLS_154 = __VLS_153({
    modelValue: (__VLS_ctx.bindRoleForm.userId),
    min: (1),
}, ...__VLS_functionalComponentArgsRest(__VLS_153));
const __VLS_156 = {}.ElSelect;
/** @type {[typeof __VLS_components.ElSelect, typeof __VLS_components.elSelect, typeof __VLS_components.ElSelect, typeof __VLS_components.elSelect, ]} */ ;
// @ts-ignore
const __VLS_157 = __VLS_asFunctionalComponent(__VLS_156, new __VLS_156({
    modelValue: (__VLS_ctx.bindRoleForm.roleId),
    placeholder: "角色",
    ...{ style: {} },
}));
const __VLS_158 = __VLS_157({
    modelValue: (__VLS_ctx.bindRoleForm.roleId),
    placeholder: "角色",
    ...{ style: {} },
}, ...__VLS_functionalComponentArgsRest(__VLS_157));
__VLS_159.slots.default;
for (const [r] of __VLS_getVForSourceType((__VLS_ctx.roles))) {
    const __VLS_160 = {}.ElOption;
    /** @type {[typeof __VLS_components.ElOption, typeof __VLS_components.elOption, ]} */ ;
    // @ts-ignore
    const __VLS_161 = __VLS_asFunctionalComponent(__VLS_160, new __VLS_160({
        key: (r.id),
        label: (`${r.roleName}(${r.roleCode})`),
        value: (r.id),
    }));
    const __VLS_162 = __VLS_161({
        key: (r.id),
        label: (`${r.roleName}(${r.roleCode})`),
        value: (r.id),
    }, ...__VLS_functionalComponentArgsRest(__VLS_161));
}
var __VLS_159;
const __VLS_164 = {}.ElButton;
/** @type {[typeof __VLS_components.ElButton, typeof __VLS_components.elButton, typeof __VLS_components.ElButton, typeof __VLS_components.elButton, ]} */ ;
// @ts-ignore
const __VLS_165 = __VLS_asFunctionalComponent(__VLS_164, new __VLS_164({
    ...{ 'onClick': {} },
}));
const __VLS_166 = __VLS_165({
    ...{ 'onClick': {} },
}, ...__VLS_functionalComponentArgsRest(__VLS_165));
let __VLS_168;
let __VLS_169;
let __VLS_170;
const __VLS_171 = {
    onClick: (__VLS_ctx.handleBindRole)
};
__VLS_167.slots.default;
var __VLS_167;
__VLS_asFunctionalElement(__VLS_intrinsicElements.div, __VLS_intrinsicElements.div)({
    ...{ style: {} },
});
const __VLS_172 = {}.ElSelect;
/** @type {[typeof __VLS_components.ElSelect, typeof __VLS_components.elSelect, typeof __VLS_components.ElSelect, typeof __VLS_components.elSelect, ]} */ ;
// @ts-ignore
const __VLS_173 = __VLS_asFunctionalComponent(__VLS_172, new __VLS_172({
    modelValue: (__VLS_ctx.bindPermForm.roleId),
    placeholder: "角色",
    ...{ style: {} },
}));
const __VLS_174 = __VLS_173({
    modelValue: (__VLS_ctx.bindPermForm.roleId),
    placeholder: "角色",
    ...{ style: {} },
}, ...__VLS_functionalComponentArgsRest(__VLS_173));
__VLS_175.slots.default;
for (const [r] of __VLS_getVForSourceType((__VLS_ctx.roles))) {
    const __VLS_176 = {}.ElOption;
    /** @type {[typeof __VLS_components.ElOption, typeof __VLS_components.elOption, ]} */ ;
    // @ts-ignore
    const __VLS_177 = __VLS_asFunctionalComponent(__VLS_176, new __VLS_176({
        key: (r.id),
        label: (r.roleName),
        value: (r.id),
    }));
    const __VLS_178 = __VLS_177({
        key: (r.id),
        label: (r.roleName),
        value: (r.id),
    }, ...__VLS_functionalComponentArgsRest(__VLS_177));
}
var __VLS_175;
const __VLS_180 = {}.ElSelect;
/** @type {[typeof __VLS_components.ElSelect, typeof __VLS_components.elSelect, typeof __VLS_components.ElSelect, typeof __VLS_components.elSelect, ]} */ ;
// @ts-ignore
const __VLS_181 = __VLS_asFunctionalComponent(__VLS_180, new __VLS_180({
    modelValue: (__VLS_ctx.bindPermForm.permissionId),
    placeholder: "权限",
    ...{ style: {} },
}));
const __VLS_182 = __VLS_181({
    modelValue: (__VLS_ctx.bindPermForm.permissionId),
    placeholder: "权限",
    ...{ style: {} },
}, ...__VLS_functionalComponentArgsRest(__VLS_181));
__VLS_183.slots.default;
for (const [p] of __VLS_getVForSourceType((__VLS_ctx.permissions))) {
    const __VLS_184 = {}.ElOption;
    /** @type {[typeof __VLS_components.ElOption, typeof __VLS_components.elOption, ]} */ ;
    // @ts-ignore
    const __VLS_185 = __VLS_asFunctionalComponent(__VLS_184, new __VLS_184({
        key: (p.id),
        label: (`${p.permName}(${p.permCode})`),
        value: (p.id),
    }));
    const __VLS_186 = __VLS_185({
        key: (p.id),
        label: (`${p.permName}(${p.permCode})`),
        value: (p.id),
    }, ...__VLS_functionalComponentArgsRest(__VLS_185));
}
var __VLS_183;
const __VLS_188 = {}.ElButton;
/** @type {[typeof __VLS_components.ElButton, typeof __VLS_components.elButton, typeof __VLS_components.ElButton, typeof __VLS_components.elButton, ]} */ ;
// @ts-ignore
const __VLS_189 = __VLS_asFunctionalComponent(__VLS_188, new __VLS_188({
    ...{ 'onClick': {} },
}));
const __VLS_190 = __VLS_189({
    ...{ 'onClick': {} },
}, ...__VLS_functionalComponentArgsRest(__VLS_189));
let __VLS_192;
let __VLS_193;
let __VLS_194;
const __VLS_195 = {
    onClick: (__VLS_ctx.handleBindPerm)
};
__VLS_191.slots.default;
var __VLS_191;
var __VLS_119;
const __VLS_196 = {}.ElTabPane;
/** @type {[typeof __VLS_components.ElTabPane, typeof __VLS_components.elTabPane, typeof __VLS_components.ElTabPane, typeof __VLS_components.elTabPane, ]} */ ;
// @ts-ignore
const __VLS_197 = __VLS_asFunctionalComponent(__VLS_196, new __VLS_196({
    label: "操作日志",
    name: "log",
}));
const __VLS_198 = __VLS_197({
    label: "操作日志",
    name: "log",
}, ...__VLS_functionalComponentArgsRest(__VLS_197));
__VLS_199.slots.default;
__VLS_asFunctionalElement(__VLS_intrinsicElements.div, __VLS_intrinsicElements.div)({
    ...{ style: {} },
});
const __VLS_200 = {}.ElButton;
/** @type {[typeof __VLS_components.ElButton, typeof __VLS_components.elButton, typeof __VLS_components.ElButton, typeof __VLS_components.elButton, ]} */ ;
// @ts-ignore
const __VLS_201 = __VLS_asFunctionalComponent(__VLS_200, new __VLS_200({
    ...{ 'onClick': {} },
}));
const __VLS_202 = __VLS_201({
    ...{ 'onClick': {} },
}, ...__VLS_functionalComponentArgsRest(__VLS_201));
let __VLS_204;
let __VLS_205;
let __VLS_206;
const __VLS_207 = {
    onClick: (__VLS_ctx.loadLogs)
};
__VLS_203.slots.default;
var __VLS_203;
const __VLS_208 = {}.ElTable;
/** @type {[typeof __VLS_components.ElTable, typeof __VLS_components.elTable, typeof __VLS_components.ElTable, typeof __VLS_components.elTable, ]} */ ;
// @ts-ignore
const __VLS_209 = __VLS_asFunctionalComponent(__VLS_208, new __VLS_208({
    data: (__VLS_ctx.pagedLogs),
}));
const __VLS_210 = __VLS_209({
    data: (__VLS_ctx.pagedLogs),
}, ...__VLS_functionalComponentArgsRest(__VLS_209));
__VLS_asFunctionalDirective(__VLS_directives.vLoading)(null, { ...__VLS_directiveBindingRestFields, value: (__VLS_ctx.logLoading) }, null, null);
__VLS_211.slots.default;
const __VLS_212 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
const __VLS_213 = __VLS_asFunctionalComponent(__VLS_212, new __VLS_212({
    prop: "id",
    label: "ID",
    width: "80",
}));
const __VLS_214 = __VLS_213({
    prop: "id",
    label: "ID",
    width: "80",
}, ...__VLS_functionalComponentArgsRest(__VLS_213));
const __VLS_216 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
const __VLS_217 = __VLS_asFunctionalComponent(__VLS_216, new __VLS_216({
    prop: "userId",
    label: "用户ID",
    width: "90",
}));
const __VLS_218 = __VLS_217({
    prop: "userId",
    label: "用户ID",
    width: "90",
}, ...__VLS_functionalComponentArgsRest(__VLS_217));
const __VLS_220 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
const __VLS_221 = __VLS_asFunctionalComponent(__VLS_220, new __VLS_220({
    prop: "action",
    label: "动作",
}));
const __VLS_222 = __VLS_221({
    prop: "action",
    label: "动作",
}, ...__VLS_functionalComponentArgsRest(__VLS_221));
const __VLS_224 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
const __VLS_225 = __VLS_asFunctionalComponent(__VLS_224, new __VLS_224({
    prop: "targetType",
    label: "目标类型",
    width: "120",
}));
const __VLS_226 = __VLS_225({
    prop: "targetType",
    label: "目标类型",
    width: "120",
}, ...__VLS_functionalComponentArgsRest(__VLS_225));
const __VLS_228 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
const __VLS_229 = __VLS_asFunctionalComponent(__VLS_228, new __VLS_228({
    prop: "targetId",
    label: "目标ID",
    width: "90",
}));
const __VLS_230 = __VLS_229({
    prop: "targetId",
    label: "目标ID",
    width: "90",
}, ...__VLS_functionalComponentArgsRest(__VLS_229));
const __VLS_232 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
const __VLS_233 = __VLS_asFunctionalComponent(__VLS_232, new __VLS_232({
    prop: "detail",
    label: "详情",
}));
const __VLS_234 = __VLS_233({
    prop: "detail",
    label: "详情",
}, ...__VLS_functionalComponentArgsRest(__VLS_233));
var __VLS_211;
__VLS_asFunctionalElement(__VLS_intrinsicElements.div, __VLS_intrinsicElements.div)({
    ...{ style: {} },
});
const __VLS_236 = {}.ElPagination;
/** @type {[typeof __VLS_components.ElPagination, typeof __VLS_components.elPagination, ]} */ ;
// @ts-ignore
const __VLS_237 = __VLS_asFunctionalComponent(__VLS_236, new __VLS_236({
    currentPage: (__VLS_ctx.logPage),
    pageSize: (__VLS_ctx.logPageSize),
    pageSizes: ([10, 20, 50]),
    layout: "total, sizes, prev, pager, next",
    total: (__VLS_ctx.logs.length),
}));
const __VLS_238 = __VLS_237({
    currentPage: (__VLS_ctx.logPage),
    pageSize: (__VLS_ctx.logPageSize),
    pageSizes: ([10, 20, 50]),
    layout: "total, sizes, prev, pager, next",
    total: (__VLS_ctx.logs.length),
}, ...__VLS_functionalComponentArgsRest(__VLS_237));
var __VLS_199;
var __VLS_3;
var __VLS_dollars;
const __VLS_self = (await import('vue')).defineComponent({
    setup() {
        return {
            activeTab: activeTab,
            categories: categories,
            sessions: sessions,
            logs: logs,
            roles: roles,
            permissions: permissions,
            categoryLoading: categoryLoading,
            sessionLoading: sessionLoading,
            logLoading: logLoading,
            logPage: logPage,
            logPageSize: logPageSize,
            categoryForm: categoryForm,
            sessionForm: sessionForm,
            roleForm: roleForm,
            permForm: permForm,
            bindRoleForm: bindRoleForm,
            bindPermForm: bindPermForm,
            pagedLogs: pagedLogs,
            loadLogs: loadLogs,
            handleCreateCategory: handleCreateCategory,
            handleDeleteCategory: handleDeleteCategory,
            handleEditCategory: handleEditCategory,
            handleCreateSession: handleCreateSession,
            handleDeleteSession: handleDeleteSession,
            handleEditSession: handleEditSession,
            handleCreateRole: handleCreateRole,
            handleCreatePerm: handleCreatePerm,
            handleBindRole: handleBindRole,
            handleBindPerm: handleBindPerm,
        };
    },
});
export default (await import('vue')).defineComponent({
    setup() {
        return {};
    },
});
; /* PartiallyEnd: #4569/main.vue */
