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
router.get('/:id', function (req, res, next) {
  const queryParam = req.params.id;
  // git rev-list --parents -n 1 48afa4ef76855468206510085930c0005495cec9

  const commit_ids_commands = `
  cd ..
  git rev-list --parents -n 1 ${queryParam}
  `;

  // console.log(commit_ids_commands);

  cmd.get(commit_ids_commands, (err_id, data_id, stderr_id) => {
    // console.log('err----', err_id);
    // console.log('data----', data_id);
    // console.log('stderr----', err_id);


    const diff_command = `git diff ${data_id} --color-words | aha > doc/files/${queryParam}.html`

    const command =
      `
    cd ..
    ${diff_command.replace(/(\r\n|\n|\r)/gm, "")}
    `;

    // console.log('command------------------------: ',);

    cmd.get(command, (err, data, stderr) => {
      // console.log('data-------->', data);
      const redirectUrl = `static/${queryParam}.html`
      // res.render('commit', { title: 'view commit', param: req.params.id });
      res.redirect(redirectUrl);
    });

  });

  // cmd.get(command, (err, data, stderr) => {
  //   const redirectUrl = `static/${queryParam}.html`
  //   // res.render('commit', { title: 'view commit', param: req.params.id });
  //   res.redirect(redirectUrl);
  // });
});

module.exports = router;
