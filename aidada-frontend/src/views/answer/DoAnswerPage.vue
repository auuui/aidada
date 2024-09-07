<template xmlns:width="http://www.w3.org/1999/xhtml">
  <div id="doAnswerPage">
    <a-card>
      <h1>{{ app?.appName }}</h1>
      <p>{{ app?.appDesc }}</p>
      <h2 style="margin-bottom: 16px">
        {{ current }}、
        {{ currentQuestion?.title }}
      </h2>
      <div>
        <a-radio-group
          direction="vertical"
          v-model="currentAnswer"
          :options="questionOptions"
          @change="doRadioChange"
        />
      </div>
      <div style="margin-top: 24px">
        <a-space size="large">
          <a-button
            type="primary"
            circle
            v-if="current < questionContent.length"
            :disabled="!currentAnswer"
            @click="current += 1"
          >
            下一题
          </a-button>
          <a-button
            type="primary"
            v-if="current === questionContent.length"
            circle
            @click="doSubmit"
          >
            {{ submitting ? "测评中" : "查看结果" }}
          </a-button>
          <a-button v-if="current > 1" circle @click="current -= 1">
            上一题
          </a-button>
        </a-space>
      </div>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import {
  computed,
  defineProps,
  reactive,
  ref,
  watchEffect,
  withDefaults,
} from "vue";
import message from "@arco-design/web-vue/es/message";
import { useRouter } from "vue-router";
import API from "@/api";
import { useLoginUserStore } from "@/store/userStore";
import {
  addAppUsingPost,
  editAppUsingPost,
  getAppVoByIdUsingGet,
} from "@/api/appController";
import { APP_SCORING_STRATEGY_MAP, APP_TYPE_MAP } from "@/constant/app";
import PictureUploader from "@/components/PictureUploader.vue";
import {
  addQuestionUsingPost,
  editQuestionUsingPost,
  listQuestionVoByPageUsingPost,
} from "@/api/questionController";
import {
  addUserAnswerUsingPost,
  generateUserAnswerIdUsingGet,
} from "@/api/userAnswerController";

interface Props {
  appId: string;
}

const props = withDefaults(defineProps<Props>(), {
  appId: () => {
    return "";
  },
});

const app = ref<API.AppVO>();
/**
 * 题目内容结构(题目列表)
 */
const questionContent = ref<API.QuestionContentDTO[]>([]);

// 当前题目序号（从 1 开始）
const current = ref(1);
// 当前题目
const currentQuestion = ref<API.QuestionContentDTO>({});
//当前题目选项
const questionOptions = computed(() => {
  return currentQuestion.value?.options
    ? currentQuestion.value.options.map((option) => ({
        label: `${option.key}. ${option.value}`,
        value: option.key,
      }))
    : [];
});

// 当前答案
const currentAnswer = ref<string>();
// 回答列表
const answerList = reactive<string[]>([]);

//是否正在提交
const submitting = ref(false);

// 唯一 id
const id = ref<number>();

// 生成唯一 id
const generateId = async () => {
  let res: any = await generateUserAnswerIdUsingGet();
  if (res.data.code === 0) {
    id.value = res.data.data as any;
  } else {
    message.error("获取唯一 id 失败，" + res.data.message);
  }
};

//进入页面时，生成唯一id
watchEffect(() => {
  generateId();
});

/**
 * 添加题目选项
 * @param question
 * @param index
 */
const addQuestionOption = (question: API.QuestionContentDTO, index: number) => {
  if (!question.options) {
    question.options = [];
  }
  question.options.splice(index, 0, {
    key: "",
    value: "",
  });
};

/**
 * 删除题目选项
 * @param question
 * @param index
 */
const deleteQuestionOption = (
  question: API.QuestionContentDTO,
  index: number
) => {
  if (!question.options) {
    question.options = [];
  }
  question.options.splice(index, 1);
};

/**
 * 添加题目
 * @param index
 */
const addQuestion = (index: number) => {
  questionContent.value.splice(index, 0, {
    title: "",
    options: [],
  });
};

/**
 * 删除题目
 * @param index
 */
const deleteQuestion = (index: number) => {
  questionContent.value.splice(index, 1);
};
/**
 * 加载数据
 */
const loadData = async () => {
  if (!props.appId) {
    return;
  }
  //获取app
  let res: any = await getAppVoByIdUsingGet({
    id: props.appId as any,
  });
  if (res.data.code === 0) {
    app.value = res.data.data as any;
  } else {
    message.error("获取应用失败" + res.data.message);
  }
  // 获取题目
  res = await listQuestionVoByPageUsingPost({
    appId: props.appId as any,
    current: 1,
    pageSize: 1,
    sortField: "createTime",
    sortOrder: "descend",
  });
  if (res.data.code === 0 && res.data.data?.records) {
    questionContent.value = res.data.data.records[0].questionContent;
  } else {
    message.error("获取题目失败" + res.data.message);
  }
};

//加载旧数据
watchEffect(() => {
  loadData();
});

watchEffect(() => {
  currentQuestion.value = questionContent.value[current.value - 1];
  currentAnswer.value = answerList[current.value - 1];
});

//选中选项后保存选项记录
const doRadioChange = (value: string) => {
  answerList[current.value - 1] = value;
};

const router = useRouter();
const loginUserStore = useLoginUserStore();

/**
 * 提交表单
 * @param data
 */
const doSubmit = async () => {
  if (!props.appId || !answerList) {
    return;
  }
  submitting.value = true;
  const res = await addUserAnswerUsingPost({
    appId: props.appId as any,
    choices: answerList,
    id: id.value as any,
  });

  if (res.data.code === 0 && res.data.data) {
    router.push(`/answer/result/${res.data.data}`);
  } else {
    message.error("提交答案失败，" + res.data.message);
  }
  submitting.value = false;
};
</script>
