var express = require('express');
var router = express.Router();
var fs = require('fs');
var cmd = require('node-cmd');
var obj = JSON.parse(fs.readFileSync('./all-log.json', 'utf8'));

/* GET home page. */
router.get('/', function (req, res, next) {
  res.render('index', { title: 'GIT COMMIT', obj: obj });
});


/* GET commit details. */
router.get('/commit/:id', function (req, res, next) {
  const queryParam = req.params.id;
  // git rev-list --parents -n 1 48afa4ef76855468206510085930c0005495cec9
  const commit_ids_commands = `
  cd ..
  git rev-list --parents -n 1 ${queryParam}
  `;
  cmd.get(commit_ids_commands, (err_id, data_id, stderr_id) => {
    const diff_command = `git diff ${data_id} --color-words | aha > doc/files/${queryParam}.html`
    const command =
      `
    cd ..
    ${diff_command.replace(/(\r\n|\n|\r)/gm, "")}
    `;
    cmd.get(command, (err, data, stderr) => {
      const redirectUrl = `/static/${queryParam}.html`
      res.redirect(redirectUrl);
    });
  });
});


/* GET documentation page. */
router.get('/doc', function (req, res, next) {
  res.render('doc', { title: 'Documentation'});
});


/* GET about page. */
router.get('/about', function (req, res, next) {
  res.render('about', { title: 'About the system'});
});

/* GET about page. */
router.get('/diagram', function (req, res, next) {
  res.render('diagram', { title: 'Microservices Diagram'});
});

/* GET identity-ms page. */
router.get('/microservices/identity-ms', function (req, res, next) {
  res.render('identity-ms', { title: 'Identity-ms'});
});
/* GET cause-ms page. */
router.get('/microservices/cause-ms', function (req, res, next) {
  res.render('cause-ms', { title: 'Cause-ms'});
});
/* GET customer-ms page. */
router.get('/microservices/customer-ms', function (req, res, next) {
  res.render('customer-ms', { title: 'Customer-ms'});
});
/* GET ledger-ms page. */
router.get('/microservices/ledger-ms', function (req, res, next) {
  res.render('ledger-ms', { title: 'Ledger-ms/Accounting-ms'});
});
/* GET rhythm-ms page. */
router.get('/microservices/rhythm-ms', function (req, res, next) {
  res.render('rhythm-ms', { title: 'Rhythm-ms'});
});


module.exports = router;
