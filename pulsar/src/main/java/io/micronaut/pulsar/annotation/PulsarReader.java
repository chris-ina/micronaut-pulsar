/*
 * Copyright 2017-2022 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.pulsar.annotation;

import io.micronaut.aop.Around;
import io.micronaut.aop.Introduction;
import io.micronaut.context.annotation.AliasFor;
import io.micronaut.messaging.annotation.MessageMapping;
import io.micronaut.pulsar.MessageSchema;
import org.apache.pulsar.common.schema.KeyValueEncodingType;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

import static io.micronaut.pulsar.MessageSchema.BYTES;
import static io.micronaut.pulsar.config.AbstractPulsarConfiguration.TOPIC_NAME_VALIDATOR;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Create and inject Pulsar reader into field.
 *
 * @author Haris Secic
 * @since 1.0
 */
@Documented
@Retention(RUNTIME)
@Target({PARAMETER, FIELD, METHOD})
@Around
@Introduction
public @interface PulsarReader {

    /**
     * @return topic name to listen to
     * @see #topic()
     */
    @AliasFor(member = "topic")
    @AliasFor(annotation = MessageMapping.class, member = "value")
    String value() default "";

    /**
     * Only single topic subscription possible for readers.
     *
     * @return topic name to listen to
     */
    @AliasFor(member = "value")
    @AliasFor(annotation = MessageMapping.class, member = "value")
    @Pattern(regexp = TOPIC_NAME_VALIDATOR)
    String topic() default "";

    /**
     * @return Subscription to connect to.
     */
    String subscriptionName() default "";

    /**
     * Defaults to {@link MessageSchema#BYTES} as default value for Pulsar {@link org.apache.pulsar.client.api.Schema}
     * is {@code byte[]}.
     *
     * @return Schema to use with pulsar topic consumer
     */
    MessageSchema schema() default BYTES;

    /**
     * If argument annotated with {@link PulsarReader} is of {@link org.apache.pulsar.common.schema.KeyValue} it's
     * possible to choose different schema for key transfer.
     *
     * @return Schema to use while parsing message key from Pulsar message
     */
    MessageSchema keySchema() default BYTES;

    /**
     * If argument annotated with {@link PulsarReader} is of {@link org.apache.pulsar.common.schema.KeyValue}
     * it's possible to choose where to get the message key from. Otherwise, this attribute is ignored.
     *
     * @return Whether to read key from the message payload or separately.
     */
    KeyValueEncodingType keyEncoding() default KeyValueEncodingType.INLINE;

    /**
     * @return Reader name.
     */
    String readerName() default "";

    /**
     * By default, reader should subscribe in non-blocking manner using default {@link java.util.concurrent.CompletableFuture}
     * of {@link org.apache.pulsar.client.api.ConsumerBuilder#subscribeAsync()}.
     * <p>
     * If blocking is set to false, application thread initializing it will block until consumer is successfully subscribed.
     *
     * @return Should the consumer subscribe in async manner or blocking
     */
    boolean subscribeAsync() default true;

    /**
     * @return Whether to position reader to the newest available message in queue or not.
     */
    boolean startMessageLatest() default true;

    /**
     * Ignored on {@link org.apache.pulsar.client.api.Reader#readNextAsync()}.
     * Use -1 for no timeout (default).
     *
     * @return Maximum allowed read time.
     */
    @Min(0)
    int readTimeout() default 0;

    /**
     * Ignored on {@link org.apache.pulsar.client.api.Reader#readNextAsync()} or if
     * {@link #readTimeout()} is 0.
     *
     * @return Time unit for {@link #readTimeout()}.
     */
    TimeUnit timeoutUnit() default TimeUnit.SECONDS;
}
