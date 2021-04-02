<template>
    <div class="roles-add">
        <component-form :data="form" :width="80">
            <template slot="formItem">
                <el-tree ref="tree" :data="permissionsTree" show-checkbox node-key="id"
                        :default-expanded-keys="[2]"
                        :default-checked-keys="[5]"
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
import { add } from '@/api/roles'
import { getPermissionsTree } from '@/api/menus'

export default {
    name:'roles-add',
    components:{},
    data () {
        return {
            dataTree:[],
            form:{
                labelWidth:'200px',
                formFields:{
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
                                    add(this.form.formFields).then(res => {
                                        if (res.data.code == 1){
                                            this.success(res.data.msg);
                                            this.$emit("handleGetTableData");
                                        }else{
                                            this.error(res.data.msg);
                                        }
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
            defaultProps: {
                children: 'children',
                label: 'title'
            },
            permissionsTree:[]
        }
    },
    created (){
        this.$nextTick(() => {
            getPermissionsTree().then(res => {
                this.jumpUrl(res.data,this,(data) => {
                    //handleCount(data,0);
                    // this.form.formLabel[2].options = data;
                    this.permissionsTree = data;
                })
            });
        });
    },
    methods:{
        change(val){
            this.form.pid = val;
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

</style>