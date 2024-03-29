const { compareSync, genSaltSync, hashSync } = require('bcrypt');
const { sign } = require('jsonwebtoken');
const mongoose = require('mongoose');
const { JWT_SECRET } = require('../shared/consts');

const Schema = new mongoose.Schema({
  name: {
    type: String,
    default: 'Anonymous',
  },
  email: {
    type: String,
    required: true,
    unique: true,
  },
  password: {
    type: String,
    required: true,
  },
});

Schema.methods.compare = async function (password) {
  const result = compareSync(password, this.password);
  if (result) return Promise.resolve();
  else throw new Error("passwords don't match");
};

Schema.methods.token = async function () {
  const token = sign({ uid: this._id }, JWT_SECRET);
  return token;
};

Schema.statics.hash = async function (password) {
  const salt = genSaltSync(12);
  const hash = hashSync(password, salt);
  return hash;
};

Schema.statics.checkEmailUsed = async function (email) {
  const user = await this.findOne({ email });
  if (user) throw new Error('user exists!');
  return Promise.resolve();
};

Schema.statics.basicLogin = async function (email, password) {
  const user = await this.findOne({ email });
  if (!user) throw new Error();
  await user.compare(password);
  const token = await user.token();
  return token;
};

Schema.statics.register = async function (email, pw, name = undefined) {
  const password = await this.hash(pw);
  const user = await this.create({ email, password, name });
  const token = await user.token();
  return token;
};

Schema.statics.getUserData = async function (id) {
  const user = await this.findById(id).select('name');
  return user;
};

const model = mongoose.model('User', Schema);

module.exports = model;
