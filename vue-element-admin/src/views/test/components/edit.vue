<template>
    <div class="menu-edit">
        <component-form :data="form" :width="80">
            <template slot="formItem">
                <el-tree ref="tree" :data="permissionsTree" show-checkbox node-key="id"
                     :default-expanded-keys="defaultCheckedKeys"
                     :default-checked-keys="defaultCheckedKeys"
                     :props="defaultProps">
                    <span class="custom-tree-node" slot-scope="{ node, data }">
                        <span>{{ node.label }}</span>
                        <el-tag v-if='node.level==1' size="mini">目录</el-tag>
                        <el-tag v-if='node.level==2' type="success" size="mini">菜单</el-tag>
                        <el-tag v-if='node.level==3' type="warning" size="mini">按钮</el-tag>
                    </span>
                </el-tree>
            </template>
        </component-form>
    </div>
</template>
<script>
import { getPermissionsIds,edit } from '@/api/roles'
import { getPermissionsTree } from '@/api/menus'

export default {
    name:'menu-edit',
    components:{},
    data () {
        return {
            loading:false,
            dataTree:[],
            defaultProps: {
                children: 'children',
                label: 'title'
            },
            defaultCheckedKeys:[],
            permissionsTree:[],
            form:{
                labelWidth:'200px',
                formFields:{
                    id:'',
                    name: '',
                    description:'',
                    status:1,
                    permissionsIds:[]
                },
                formLabel:[
                    {prop: 'name', title: '角色名称', type: 'input',width:'80%',placeholder:'请输入姓名'},
                    {prop: 'description', title: '角色描述', type: 'input',width:'80%',placeholder:'请输入姓名'},
                    {prop: 'text', title: '选择权限', type: 'slot',slot:'formItem'},
                ],
                buttons:{
                    options:[
                        {title:'提交',type:'primary',loading:false,click:(form,item) => {
                                this.form.formFields.permissionsIds = this.$refs.tree.getCheckedKeys();
                                item.loading = true;
                                form.validate(valid => {
                                    if (valid){
                                        edit(this.form.formFields).then(res => {
                                            if (res.data.code == 1){
                                                this.success(res.data.msg);
                                                this.$emit("handleGetTableData");
                                            }else{
                                                this.error(res.data.msg);
                                            }
                                            item.loading = false;
                                        });
                                    }
                                });
                            }},
                    ]
                },
                rules: {
                    id: [
                        { required: true},
                    ],
                    name: [
                        { required: true, message: '请输入活动名称', trigger: 'blur' },
                        { min: 1, max: 20, message: '长度在 20 个字符内', trigger: 'blur' }
                    ],
                }
            },
        }
    },
    created (){
        this.$nextTick(() => {
            getPermissionsTree().then(res => {
                this.jumpUrl(res.data,this,(data) => {
                    this.permissionsTree = data;
                })
            });
        });
    },
    methods:{
        currentData(currentData){
            currentData && Object.keys(this.form.formFields).forEach(key => this.form.formFields[key] = currentData[key]);
            if (currentData){
                getPermissionsIds(currentData.id).then(res => {
                    this.jumpUrl(res.data,this,(data) => {
                        this.defaultCheckedKeys = data;
                    })
                })
            }
        },
        change(val){
            this.form.pid = val;
        },
        handle(node,data){
            console.log(node);
            console.log(data);
        },
        handleSumit(){
            this.$refs['form'].validate(valid => {
                if (valid){
                    add(this.form).then(res => {
                        if (res.data.code == 1){
                            this.success("操作成功");
                            this.$emit("handleGetTableData");
                        }else{
                            this.error(res.data.msg);
                        }
                    })
                }
            })
        }
    }
}
</script>
<style lang="css" scoped>
.custom-tree-node {
    flex: 1;
    /*display: flex;*/
    /*align-items: center;*/
    /*justify-content: space-between;*/
    font-size: 14px;
    padding-right: 8px;
}
</style>