import { defineConfig } from 'vitepress'

// https://vitepress.dev/reference/site-config
export default defineConfig({
  title: "cm-bs-helper",
  description: "a bs-like map manager build with kotlin multiplatform",
  cleanUrls: true,
  themeConfig: {
    // https://vitepress.dev/reference/default-theme-config
    nav: [
      { text: '用例手册', link: '/examples/' },
      { text: '版本更新日志', link: '/release/' },
      { text: '反馈', link: 'https://bs-helper.api.ktlab.io/feedback' }
    ],

    sidebar: {
      '/release/': [
        {
          text: 'Release',
          items: [
            { text: '更新日志', link: '/release/' },
            { text: 'v0.0.3-alpha01', link: '/release/v0.0.3-alpha01' },
            { text: 'v0.0.2-alpha05', link: '/release/v0.0.2-alpha05' }
          ]
        }
      ],
    },

    socialLinks: [
      { icon: 'github', link: 'https://github.com/ktKongTong/cm-bs-helper' }
    ]
  }
})
