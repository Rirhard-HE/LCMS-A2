<template>
  <el-aside :width="collapsed ? '64px' : '220px'" class="lcms-aside">
    <el-menu
      router
      :collapse="collapsed"
      :default-active="$route.path"
      background-color="transparent"
      text-color="#e9eef5"
      active-text-color="#fff"
      class="lcms-menu"
    > 

    <el-menu-item index="/dashboard">
        <i class="el-icon-data-analysis"></i>
        <span class='font-size' slot="title">Dashboard</span>
      </el-menu-item>

      <el-menu-item index="/categories">
        <i class="el-icon-collection"></i>
        <span class='font-size' slot="title">Categories</span>
      </el-menu-item>

      <el-menu-item index="/cases">
        <i class="el-icon-s-order"></i>
        <span class='font-size' slot="title">Cases</span>
      </el-menu-item>

      <el-menu-item index="/evidence">
        <i class="el-icon-document"></i>
        <span class='font-size' slot="title">Evidences</span>
      </el-menu-item>

      <el-menu-item index="/hearings">
        <i class="el-icon-reading"></i>
        <span class='font-size' slot="title">Hearings</span>
      </el-menu-item>

    </el-menu>
        <div class="collapse-ctl" @click="toggle">
      <i :class="collapsed ? 'el-icon-arrow-right' : 'el-icon-arrow-left'"></i>
    </div>
  </el-aside>
</template>

<script>
export default {
  name: 'Sidebar',
  props: { value: { type: Boolean, default: false } },
  data() {
    return { collapsed: this.value }
  },
  watch: {
    value(v) { this.collapsed = v },
    collapsed(v) { this.$emit('input', v) }
  },
  methods: {
    toggle() { this.collapsed = !this.collapsed }
  }
}
</script>

<style scoped>
.collapse-ctl {
  position: absolute; left: 0; right: 0; bottom: 100px;
  display: flex; justify-content: center; align-items: center;
  cursor: pointer; color: #f7f9fb; opacity: .9;
}
.collapse-ctl i {
  font-size: 18px; padding: 6px 10px; border-radius: 999px;
  background: rgba(255,255,255,.06); border: 1px solid rgba(255,255,255,.12);
}
.lcms-aside {
  height: 100vh;
  background: rgba(41, 112, 179, 0.9);
  border-right: 1px solid rgba(255, 255, 255, 0.06);
  position: relative;
  transition: width 0.3s ease;
}
.font-size {
  color: #fff; 
  font-family: Arial, sans-serif;
}
.lcms-menu {
  border-right: none !important;
}
</style>
