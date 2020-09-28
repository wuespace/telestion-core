/**
 * Converts the output of the GitHub Super Linter (from a super-linter.log 
 * file in the same directory) to GitHub annotations. Meant to be run in a
 * GitHub Action workflow.
 *
 * @author Pablo Klaschka <contact@pabloklaschka.de>
 */

const fs = require('fs');
const path = require('path');

const contents = fs.readFileSync(
	path.join(__dirname, 'super-linter.log')
).toString();

const lines = contents.split('\n');
const errors = (
	lines.filter(
		line=>line.startsWith('[ERROR]')
	)
);

for (let error of errors) {
	const results = 
		  error.match(/\[ERROR\] ([^:]+):(\d+)(:(\d+))?: (.*)/);

	const file = results[1];
	const line = results[2];
	const col = results[4];
	const message = results[5];

	console.log(
		`::error file=${file},line=${line}${col ? `,col=${col}` : ''}::${message}`
	);
}
