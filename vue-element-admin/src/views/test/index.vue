<template>
    <div class="roles">
        <h1>测试</h1>
        <component-wang-editor :disabled='disable' :height="350"></component-wang-editor>
        <!--<div class="header">
            <el-button v-has="'add'" type="primary" @click="handleTable(true,'showTableAdd')" >添加</el-button>
        </div>
        <component-table :data="tableData">
            <template v-slot:sex="{scope}">
                <el-tag v-if="scope.row.sex==1">男</el-tag>
                <el-tag v-if="scope.row.sex==2" type="danger">女</el-tag>
            </template>
            <template v-slot:button="{scope}">
                <el-button v-has="'edit'" type="primary" size="mini" @click="handleEdit(scope)">编辑</el-button>
                <component-popover v-has="'del'" :params="scope.row" @ok="handleOk" @cancel="handleCancel"></component-popover>
            </template>
        </component-table>

        <component-dialog v-if="show" :width="50" :title="optionText" :visible.sync="show" :footer="false">
            <table-add slot="dialog" v-if="showTable.showTableAdd" @handleGetTableData="handleGetTableData" style="overflow: hidden"></table-add>
            <table-edit slot="dialog" ref="tableEdit" v-if="showTable.showTableEdit" :groups="parentData" @handleGetTableData="handleGetTableData" style="overflow: hidden"></table-edit>
        </component-dialog>-->
    </div>
</template>

<script>
import tableAdd from './components/add.vue'
import tableEdit from './components/edit.vue'
import { getTableData,del } from '@/api/user'

export default {
    name: "component-roles",
    components:{
        tableAdd,
        tableEdit
    },
    data () {
        return {
            disable:true,
            show:false,
            showTable:{
                showTableAdd:false,
                showTableEdit:false,
            },
            tableDataAll:null,
            currentPage:1,
            optionText:'',
            search:{
                keywords:''
            },
            parentData:null,
            visible:false,
            tableData: {
                loading:false,
                // 请求回来的数据
                tableData:[],
                // 列
                tableLabel:[],
                // 操作
                tableOption:{
                    label:'操作',
                    width:150,
                    slot:true,
                },
                // 分页
                page:{align:'right',total:1,size:10,currentPage:1,currentChange:(currentPage) => {
                    console.log('当前页',currentPage);
                    this.tableData.loading = true;
                    setTimeout(() => {
                        this.tableData.loading = false;
                    },1500)
                }},
            },
        }
    },
    created(){
        this.disabled = false;
        //this.labelInit();
        // this.tableDataInit(this.tableData.page.currentPage,pageOffset);
        //this.handleGetTableData();
    },
    methods: {
        handleGetTableData(flag=false){
            this.handleTable(flag);
            this.labelInit();
            this.tableDataInit(this.currentPage,pageOffset);
        },
        // 列初始化
        labelInit(){
            this.tableData.tableLabel = [
                {type:'index',title:'序号',fixed:'left',width:60,align:'center'},
                {prop:'username',title:'账号'},
                {prop:'phone',title:'手机号码',width:116},
                {prop:'nickName',title:'昵称'},
                {prop:'realName',title:'真实姓名'},
                {prop:'sex',title:'性别',slot:'sex',width:60},
                {prop:'email',title:'邮箱',minWidth:118},
                {prop:'createUserName',title:'创建人'},
                {prop:'updateUserName',title:'更新人'},
                {prop:'createWhere',title:'创建来源'},
                {prop:'status',title:'状态',isSwitch:true,activeText:'正常',inactiveText:'禁用',style:(params,item)=>{if(params.row.name=='超级管理员')return {display:'none'}},change:(currentData) => {console.log('switch开关',currentData)}},
                {prop:'createTime',title:'创建时间',width:160,render:(params,col)=> [this.formattime(params.row.createTime)]},
                {prop:'updateTime',title:'更新时间',width:160,render:(params,col)=> [this.formattime(params.row.updateTime)]}
            ];
        },
        // 数据初始化
        tableDataInit(currentPage,pageOffset,keywords='') {
            this.tableData.loading = true;
            getTableData(this.search).then(res => {
                this.jumpUrl(res.data,this,data => {
                    this.tableData.loading = false;
                    this.tableData.tableData = data.rows;
                    console.log(data.rows);
                    // this.tableDataAll = res.data.data.data;
                    // this.tableData.tableData = this.handleTableData(this.tableDataAll,0,pageOffset);
                    // this.parentData = res.data.data.parent;
                    // this.tableData.page.total = this.tableDataAll.length;
                });
            });
        },
        handleTable(flag,type='',text='新增角色'){
            ObjectforIn(this.showTable,false);
            this.optionText = text;
            this.show = flag;
            this.showTable[type] = flag;
        },
        // 截取数组中指定的项
        handleTableData (tableData,start,end) {
            return Array.isArray(tableData) &&　tableData.slice(start,start+end);
        },
        /****************************** 操作 ******************************/
        handleEdit(params){
            this.$nextTick(async () => {
                await this.handleTable(true, 'showTableEdit', '编辑角色');
                this.$refs.tableEdit && this.$refs.tableEdit.currentData(params.row)
            })
        },
        handleOk(params){
            del(params.id).then(res => {
                if (res.data.code == 1){
                    (this.tableData.tableData.length == 1 && this.currentPage > 1)&&(--this.currentPage);
                    this.handleGetTableData();
                    return this.success(res.data.msg);
                }
                return this.error(res.data.msg);
            });
        },
        handleCancel(params){
            return this.warning('取消删除');
        },
    },
}
var getTreeTable = function (data) {
    let tableData = [];
    if (data && data.length > 0){
        data.map(item => {
            item.children = [];
            data.map((child,index) => {
                if (item.id == child.pid){
                    child.parent = item.title;
                    item.children.push(child);
                }
            });
            tableData.push(item);
        });
    }
    return tableData.filter(item => item.pid == '0');
}
</script>

<style lang="css" scoped>

</style>