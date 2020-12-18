package com.zwrss.privatechat.configuration

import java.io.File

trait PropertyStoreBuilder {
  def withArgs(args: Array[String]): PropertyStoreBuilder
  def withFile(file: File): PropertyStoreBuilder
  def withEnvVariables(): PropertyStoreBuilder
  def build: PropertyStore
}

case class PropertyStoreHolder(propertyStore: PropertyStore) extends PropertyStoreBuilder {
  override def withArgs(args: Array[String]): PropertyStoreBuilder = PropertyStoreHolder(CompositePropertyStore(propertyStore, EqualsMapStringsPropertyStore(args)))

  override def withFile(file: File): PropertyStoreBuilder = PropertyStoreHolder(CompositePropertyStore(propertyStore, FilePropertyStore(file)))

  override def withEnvVariables(): PropertyStoreBuilder = PropertyStoreHolder(CompositePropertyStore(propertyStore, EnvVariablesPropertyStore))

  override def build: PropertyStore = propertyStore
}

object PropertyStoreBuilder extends PropertyStoreBuilder {
  override def withArgs(args: Array[String]): PropertyStoreBuilder = PropertyStoreHolder(EqualsMapStringsPropertyStore(args))

  override def withFile(file: File): PropertyStoreBuilder = PropertyStoreHolder(FilePropertyStore(file))

  override def withEnvVariables(): PropertyStoreBuilder = PropertyStoreHolder(EnvVariablesPropertyStore)

  override def build: PropertyStore = throw new NoSuchElementException("Empty property store builder!")
}
