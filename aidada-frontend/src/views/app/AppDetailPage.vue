<template>
  <div id="appDetailPage">
    <a-card>
      <a-row style="margin-bottom: 16px">
        <a-col flex="auto" class="content-wapper">
          <h2>{{ data.appName }}</h2>
          <p>{{ data.appDesc }}</p>
          <p>应用类型:{{ APP_TYPE_MAP[data.appType] }}</p>
          <p>评分策略:{{ APP_SCORING_STRATEGY_MAP[data.scoringStrategy] }}</p>
          <p>
            <a-space>
              作者:
              <div
                :style="{
                  display: 'flex',
                  alignItems: 'center',
                  color: '#1D2129',
                }"
              >
                <a-avatar
                  :size="24"
                  :image-url="data.user?.userAvatar"
                  :style="{ marginRight: '8px' }"
                />
                <a-typography-text
                  >{{ data.user?.userName ?? "无名" }}
                </a-typography-text>
              </div>
            </a-space>
          </p>
          <p>
            创建时间: {{ dayjs(data.createTime).format("YYYY-MM-DD HH:mm:ss") }}
          </p>
          <a-space size="medium">
            <a-button type="primary" :href="`/answer/do/${id}`"
              >开始答题
            </a-button>
            <a-button @click="doShare">分享应用</a-button>
            <a-button v-if="isMy" :href="`/add/question/${id}`"
              >设置题目
            </a-button>
            <a-button v-if="isMy" :href="`/add/scoring_result/${id}`"
              >设置评分
            </a-button>
            <a-button v-if="isMy" :href="`/add/app/${id}`">修改应用</a-button>
          </a-space>
        </a-col>
        <a-col flex="320px">
          <div>
            <a-image width="100%" :src="data.appIcon" />
          </div>
        </a-col>
      </a-row>
    </a-card>
  </div>
  <ShareModal :link="shareLink" title="应用分享" ref="shareModalRef" />
</template>

<script setup lang="ts">
import { computed, defineProps, ref, watchEffect, withDefaults } from "vue";
import message from "@arco-design/web-vue/es/message";
import { getAppVoByIdUsingGet } from "@/api/appController";
import { useRouter } from "vue-router";
import API from "@/api";
import { dayjs } from "@arco-design/web-vue/es/_utils/date";
import { useLoginUserStore } from "@/store/userStore";
import { APP_SCORING_STRATEGY_MAP, APP_TYPE_MAP } from "../../constant/app";
import ShareModal from "@/components/ShareModal.vue";

//初始值，不应该被修改
const initSearchParams = {
  current: 1,
  pageSize: 12,
};
const searchParams = ref<API.AppQueryRequest>({
  ...initSearchParams,
});

interface Props {
  id: string;
}

const props = withDefaults(defineProps<Props>(), {
  id: () => {
    return "";
  },
});
const data = ref<API.AppVO>({});
const router = useRouter();
//获取登录用户
const loginUserStore = useLoginUserStore();
let loginUserId = loginUserStore.loginUser.id;
//是否为本人创建的
const isMy = computed(() => {
  return loginUserId && loginUserId === data.value.userId;
});

/**
 * 加载数据
 */
const loadData = async () => {
  if (!props.id) {
    return;
  }
  // eslint-disable-next-line no-undef
  const res = await getAppVoByIdUsingGet({
    id: props.id as any,
  });
  if (res.data.code === 0) {
    data.value = res.data.data as any;
  } else {
    message.error("获取数据失败" + res.data.message);
  }
};
/**
 * 当分页变化时，改变搜索条件，触发数据加载
 */
const onPageChange = (page: number) => {
  searchParams.value = {
    ...searchParams.value,
    current: page,
  };
};

//监听searchParams 变量，改变时触发数据的重新加载
watchEffect(() => {
  loadData();
});

//分享弹窗的引用
const shareModalRef = ref();

//分享链接
const shareLink = `${window.location.protocol}//${window.location.host}/app/detail/${props.id}`;

const doShare = (e: Event) => {
  if (shareModalRef.value) {
    shareModalRef.value.openModal();
  }
  //停止冒泡，防止跳转到详情页
  e.stopPropagation();
};
</script>

<style scoped>
#appDetailPage .content-wapper > * {
  margin-bottom: 24px;
}
</style>
