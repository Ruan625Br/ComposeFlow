// @ts-check
import { defineConfig } from 'astro/config';
import starlight from '@astrojs/starlight';
import mdx from '@astrojs/mdx';

// https://astro.build/config
export default defineConfig({
    site: 'https://composeflow.github.io',
	integrations: [
		starlight({
			title: 'ComposeFlow docs',
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
                   	label: 'Advanced UI',
                    autogenerate: { directory: 'advanced_ui' },
                }, {
					label: 'Reference',
					autogenerate: { directory: 'reference' },
				},
			],
		}),
	],
});
