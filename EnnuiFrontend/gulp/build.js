var gulp = require('gulp');

gulp.task('build', ['clean','move-jsFiles', 'typedoc','templates', 'fonts', 'vendor', 'scripts', 'styles', 'images', 'data']);
