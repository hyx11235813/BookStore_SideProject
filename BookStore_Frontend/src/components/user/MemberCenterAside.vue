<template>
    <div>
        <div class="container" v-for="i in asideList" :key=i.name>
            <div class="menu" @click="clickMenu(i)">
                <div class="item" :class="{ active: $route.path === i.path }">
                    <picture><i :class="i.icon"></i></picture>
                    <div class="link-name">{{ i.label }}</div>
                </div>
            </div>
        </div>
    </div>
</template>
<script>
export default {
    data() {
        return {
            asideList: [
                { name: 'personalInfo', label: '個人資料', path: "/member-center/personal-info", icon: "el-icon-user-solid" },
            ],
        }
    }, methods: {
        clickMenu(item) {
            if (item.path !== this.$route.path) {
                this.$router.push(item.path)
            }
        }

    },
    mounted() {

    }
}
</script>
<style lang="scss" scoped>
@use '@/assets/style/abstracts' as *;

* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

.container {
    padding-top: 24px;

    .menu {
        text-decoration: none;
        color: #111;

        .item {
            display: flex;
            align-items: center;
            height: 50px;
            border-radius: 0 50px 50px 0;
            padding-left: 20px;
            cursor: pointer;

            &.active {
                background-color: $clr-bg-soft-green;
            }

            .link-name {
                flex: 1;
                margin-left: 14px;
            }
        }
    }
}






@media(max-width: 1024px) {
    .container {
        display: none;
    }
}
</style>
