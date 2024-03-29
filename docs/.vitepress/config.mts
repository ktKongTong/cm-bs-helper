import { defineConfig } from 'vitepress'

// https://vitepress.dev/reference/site-config
export default defineConfig({
  title: "cm-bs-helper",
  description: "a bs-like map manager build with kotlin multiplatform",
  cleanUrls: true,
  themeConfig: {
    aside: true,
    outline: {
      level: "deep",
    },
    // https://vitepress.dev/reference/default-theme-config
    nav: [
      { text: '示例', link: '/examples/' },
      { text: '版本更新日志', link: '/release/' },
      { text: '反馈', link: 'https://bs-helper.api.ktlab.io/feedback' },
      { text:'abs', link: 'https://abs.ktlab.io'}
    ],

    sidebar: {
      '/release/': [
        {
          text: 'Release',
          items: [
            { text: '更新日志', link: '/release/' },
            { text: 'v0.0.5', link: '/release/v0.0.5' },
            { text: 'v0.0.4', link: '/release/v0.0.4' },
          ]
        }
      ],
      '/example/': [
        {
          text: '示例',
          items: [
            { text: '新手上路', link: '/example/' },
          ]
        }
      ]
    },

    socialLinks: [
      { icon: 'github', link: 'https://github.com/ktKongTong/cm-bs-helper' }
    ]
  }
})
