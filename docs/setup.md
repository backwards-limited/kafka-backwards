# Setup

Apologies I shall only cover **Mac** - One day I may include Linux and Windows.

Install [Homebrew](https://brew.sh) for easy package management on Mac:

```bash
ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"
```

Installation essentials:

```bash
brew update
brew install scala
brew install sbt
brew install kubernetes-cli
brew install kubectl
brew cask install virtualbox
brew cask install docker
brew cask install minikube
```

Other essentials related to Kafak:

```bash
brew tap confluentinc/homebrew-confluent-hub-client
brew cask install confluent-hub-client
```

As well as the above essentials, we can also install the following to aid testing/understanding:

```bash
brew install kafka
brew install kafkacat
brew install elasticsearch
```