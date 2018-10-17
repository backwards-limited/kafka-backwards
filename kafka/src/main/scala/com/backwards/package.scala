package com

package object backwards {
  type Or[Bad, Good] = Bad Either Good
}