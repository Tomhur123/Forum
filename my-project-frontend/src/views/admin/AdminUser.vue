<script setup>

import {apiAdminUserDetails, apiAdminUserDetailsSave, apiAdminUserList} from "@/net/api/user";
import {reactive, ref, watchEffect} from "vue";
import {useStore} from "@/store";
import {EditPen} from "@element-plus/icons-vue";
import {ElMessage} from "element-plus";

const userTable = reactive({
  page: 1,
  size: 10,
  data: [],
  total: 0
})

const store = useStore()

const editor = reactive({
  id: 0,
  display: false,
  temp: {
    details: {},
    privacy: {}
  },
  loading: false
})

const handleEdit = (user) =>  {
  editor.id = user.id
  editor.display = true
  editor.loading = true
  apiAdminUserDetails(editor.id, data => {
    editor.temp = {...data, ...user}
    console.log(editor.temp)
    editor.loading = false
  })
}

function saveUserDetails() {
  apiAdminUserDetailsSave(editor.temp, () => {
    const user = userTable.data.find(user => user.id === editor.temp.id);
    Object.assign(user, editor.temp)
    ElMessage.success('用户信息保存成功！')
  })
}

watchEffect(() => apiAdminUserList(userTable.page,userTable.size, data => {
  userTable.total = data.total
  userTable.data = data.list
}))
</script>

<template>
<div class="user-admin">
  <div class="title">
    用户管理
  </div>
  <div class="desc">
    在这里你可以管理所有用户的信息
  </div>
  <div>
    <el-table :data="userTable.data" height="320">
      <el-table-column prop="id" label="编号" width="80"/>
      <el-table-column label="用户名" width="180">
        <template #default="{ row }">
          <div class="table-username">
            <el-avatar :size="30" :src="store.avatarUserUrl(row.avatar)"/>
            <div>{{row.username}}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="角色" width="100" align="center">
        <template #default="{ row }">
          <el-tag type="danger" v-if="row.role === 'admin'">管理员</el-tag>
          <el-tag v-else>普通用户</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="email" label="电子邮件"/>
      <el-table-column label="注册时间">
        <template #default="{ row }">
          <div>{{new Date(row.registerTime).toLocaleString()}}</div>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center">
        <template #default="{ row }">
          <div class="user-status">
            <el-tag type="warning" v-if="row.mute === true">禁言中</el-tag>
            <el-tag type="danger" v-if="row.banned === true">封禁中</el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center">
        <template #default="{ row }">
          <el-button type="primary" size="small" :icon="EditPen"
                     @click="handleEdit(row)"
                     :disabled="row.role === 'admin'">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="pagination">
      <el-pagination :total="userTable.total"
                     v-model:current-page="userTable.page"
                     v-model:page-size="userTable.size"
                     layout="total, sizes, prev, pager, next, jumper"/>
    </div>
    <el-drawer v-model="editor.display" v-loading="editor.loading">
      <template #header>
        <div>
          <div style="color:dodgerblue;font-weight: bold;display:flex;align-items:center">
            <el-icon><EditPen/></el-icon> 编辑用户信息
          </div>
          <div style="font-size: 13px;color: grey;">
            编辑完成后请点击下方保存按钮
          </div>
        </div>
      </template>
      <el-scrollbar height="100%">
        <div style="text-align: center">
          <el-avatar size="default" :src="store.avatarUserUrl(editor.temp.avatar)"/>
        </div>
        <el-form label-position="top">
          <el-form-item label="用户名">
            <el-input v-model="editor.temp.username"/>
          </el-form-item>
          <el-radio-group style="display: flex;gap: 80px" v-model="editor.temp.details.gender">
            <el-radio style="margin-right: 10px" :label=0>男</el-radio>
            <el-radio style="margin-right: 10px" :label=1>女</el-radio>
          </el-radio-group>
          <el-form-item label="电子邮件">
            <el-input v-model="editor.temp.email"/>
          </el-form-item>
          <el-radio-group style="display: flex;gap: 80px" v-model="editor.temp.role">
            <el-radio style="margin-right: 10px" label="user">用户</el-radio>
            <el-radio style="margin-right: 10px" label="admin">管理员</el-radio>
          </el-radio-group>
          <el-form-item label="QQ">
            <el-input v-model="editor.temp.details.qq"/>
          </el-form-item>
          <el-form-item label="微信">
            <el-input v-model="editor.temp.details.wx"/>
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="editor.temp.details.phone"/>
          </el-form-item>
          <el-form-item label="个人简介">
            <el-input autosize type="textarea" v-model="editor.temp.details.description"/>
          </el-form-item>
          <div class="admin-switch">
            <el-switch class="admin-switch-inner" v-model="editor.temp.mute"/>
            {{editor.temp.mute===true?'禁言中':'禁言'}}
            <el-divider direction="vertical" style="height: 30px"/>
            <el-switch class="admin-switch-inner" v-model="editor.temp.banned"/>
            {{editor.temp.banned === true ? '账号封禁中' : '封禁账号'}}
          </div>
          <div style="margin-top:10px;text-align: center;font-size: 12px;color: grey">
            注册时间: {{new Date(editor.temp.registerTime).toLocaleString()}}
          </div>
        </el-form>
        <el-divider style="margin-top: 10px;color: grey"/>
      </el-scrollbar>
      <template #footer>
        <div style="text-align: center">
          <el-button type="primary" @click="saveUserDetails">保存</el-button>
          <el-button type="info" @click="editor.display=false">取消</el-button>
        </div>
      </template>
    </el-drawer>
  </div>
</div>
</template>

<style scoped>
.user-admin {
  .title {
    font-size: 24px;
    font-weight: bold
  }
  .desc {
    font-size: 14px;
    color: grey;
    margin-bottom: 20px;
  }
  .table-username {
    height: 30px;
    gap: 10px;
    display: flex;
    align-items: center;
  }
  .user-status {
    display: flex;
    gap: 3px;
    justify-content: center;
  }
  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: right;
  }
  :deep(.el-drawer__header) {
    margin-bottom: 0;
  }

  .admin-switch {
    display: flex;
    gap: 20px;
    font-size: 14px;
    align-items: center;

    .admin-switch-inner {
      margin-right: 10px;
      --el-switch-on-color: #ff4949;
      --el-switch-off-color: #13ce66
    }
  }
}
</style>