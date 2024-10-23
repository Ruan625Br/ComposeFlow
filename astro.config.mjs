// @ts-check
import { defineConfig } from 'astro/config';
import starlight from '@astrojs/starlight';
import mdx from '@astrojs/mdx';

// https://astro.build/config
export default defineConfig({
	integrations: [
		starlight({
			title: 'ComposeFlow docs',
			social: {
				github: 'https://github.com/withastro/starlight',
			},
			logo: {
				src: '/logo.png',
				alt: 'ComposeFlow Logo',
			},
			sidebar: [{
                	label: 'Getting started',
                	autogenerate: { directory: 'getting_started' },
                }, {
                    label: 'Basics',
                    autogenerate: { directory: 'basics' },
                }, {
                    label: 'Firebase',
                    autogenerate: { directory: 'firebase' },
                }, {
					label: 'Guides',
                	autogenerate: { directory: 'guides' },
				}, {
					label: 'Reference',
					autogenerate: { directory: 'reference' },
				},
			],
		}),
	],
});
