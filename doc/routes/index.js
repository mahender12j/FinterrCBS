var express = require('express');
var router = express.Router();
var fs = require('fs');
var cmd = require('node-cmd');

/* GET home page. */
router.get('/', function (req, res, next) {
  var obj = JSON.parse(fs.readFileSync('./all-log.json', 'utf8'));
  res.render('index', { title: 'GIT COMMIT', obj: obj });
});


/* GET commit details. */
router.get('/:id', function (req, res, next) {
  const queryParam = req.params.id;
  const command =
    `
    cd ..
    git diff ${queryParam} --color-words | aha > doc/files/${queryParam}.html
    `;

  cmd.get(command, (err, data, stderr) => {
    const redirectUrl = `static/${queryParam}.html`
    // res.render('commit', { title: 'view commit', param: req.params.id });
    res.redirect(redirectUrl);
  });
});

module.exports = router;
