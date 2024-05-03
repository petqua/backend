package com.petqua.test.fake

import com.amazonaws.AmazonWebServiceRequest
import com.amazonaws.HttpMethod
import com.amazonaws.regions.Region
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.S3ClientOptions
import com.amazonaws.services.s3.S3ResponseMetadata
import com.amazonaws.services.s3.model.*
import com.amazonaws.services.s3.model.analytics.AnalyticsConfiguration
import com.amazonaws.services.s3.model.intelligenttiering.IntelligentTieringConfiguration
import com.amazonaws.services.s3.model.inventory.InventoryConfiguration
import com.amazonaws.services.s3.model.metrics.MetricsConfiguration
import com.amazonaws.services.s3.model.ownership.OwnershipControls
import com.amazonaws.services.s3.waiters.AmazonS3Waiters
import java.io.File
import java.io.InputStream
import java.net.URL
import java.util.Date

class FakeAmazonS3 : AmazonS3 {
    override fun putObject(putObjectRequest: PutObjectRequest?): PutObjectResult {
        TODO("Not yet implemented")
    }

    override fun putObject(bucketName: String?, key: String?, file: File?): PutObjectResult {
        TODO("Not yet implemented")
    }

    override fun putObject(
        bucketName: String?,
        key: String?,
        input: InputStream?,
        metadata: ObjectMetadata?,
    ): PutObjectResult {
        return PutObjectResult()
    }

    override fun putObject(bucketName: String?, key: String?, content: String?): PutObjectResult {
        TODO("Not yet implemented")
    }

    override fun getObject(bucketName: String?, key: String?): S3Object {
        TODO("Not yet implemented")
    }

    override fun getObject(getObjectRequest: GetObjectRequest?): S3Object {
        TODO("Not yet implemented")
    }

    override fun getObject(getObjectRequest: GetObjectRequest?, destinationFile: File?): ObjectMetadata {
        TODO("Not yet implemented")
    }

    override fun completeMultipartUpload(request: CompleteMultipartUploadRequest?): CompleteMultipartUploadResult {
        TODO("Not yet implemented")
    }

    override fun initiateMultipartUpload(request: InitiateMultipartUploadRequest?): InitiateMultipartUploadResult {
        TODO("Not yet implemented")
    }

    override fun uploadPart(request: UploadPartRequest?): UploadPartResult {
        TODO("Not yet implemented")
    }

    override fun copyPart(copyPartRequest: CopyPartRequest?): CopyPartResult {
        TODO("Not yet implemented")
    }

    override fun abortMultipartUpload(request: AbortMultipartUploadRequest?) {
        TODO("Not yet implemented")
    }

    override fun setEndpoint(endpoint: String?) {
        TODO("Not yet implemented")
    }

    override fun setRegion(region: Region?) {
        TODO("Not yet implemented")
    }

    override fun setS3ClientOptions(clientOptions: S3ClientOptions?) {
        TODO("Not yet implemented")
    }

    override fun changeObjectStorageClass(bucketName: String?, key: String?, newStorageClass: StorageClass?) {
        TODO("Not yet implemented")
    }

    override fun setObjectRedirectLocation(bucketName: String?, key: String?, newRedirectLocation: String?) {
        TODO("Not yet implemented")
    }

    override fun listObjects(bucketName: String?): ObjectListing {
        TODO("Not yet implemented")
    }

    override fun listObjects(bucketName: String?, prefix: String?): ObjectListing {
        TODO("Not yet implemented")
    }

    override fun listObjects(listObjectsRequest: ListObjectsRequest?): ObjectListing {
        TODO("Not yet implemented")
    }

    override fun listObjectsV2(bucketName: String?): ListObjectsV2Result {
        TODO("Not yet implemented")
    }

    override fun listObjectsV2(bucketName: String?, prefix: String?): ListObjectsV2Result {
        TODO("Not yet implemented")
    }

    override fun listObjectsV2(listObjectsV2Request: ListObjectsV2Request?): ListObjectsV2Result {
        TODO("Not yet implemented")
    }

    override fun listNextBatchOfObjects(previousObjectListing: ObjectListing?): ObjectListing {
        TODO("Not yet implemented")
    }

    override fun listNextBatchOfObjects(listNextBatchOfObjectsRequest: ListNextBatchOfObjectsRequest?): ObjectListing {
        TODO("Not yet implemented")
    }

    override fun listVersions(bucketName: String?, prefix: String?): VersionListing {
        TODO("Not yet implemented")
    }

    override fun listVersions(
        bucketName: String?,
        prefix: String?,
        keyMarker: String?,
        versionIdMarker: String?,
        delimiter: String?,
        maxResults: Int?,
    ): VersionListing {
        TODO("Not yet implemented")
    }

    override fun listVersions(listVersionsRequest: ListVersionsRequest?): VersionListing {
        TODO("Not yet implemented")
    }

    override fun listNextBatchOfVersions(previousVersionListing: VersionListing?): VersionListing {
        TODO("Not yet implemented")
    }

    override fun listNextBatchOfVersions(listNextBatchOfVersionsRequest: ListNextBatchOfVersionsRequest?): VersionListing {
        TODO("Not yet implemented")
    }

    override fun getS3AccountOwner(): Owner {
        TODO("Not yet implemented")
    }

    override fun getS3AccountOwner(getS3AccountOwnerRequest: GetS3AccountOwnerRequest?): Owner {
        TODO("Not yet implemented")
    }

    override fun doesBucketExist(bucketName: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun doesBucketExistV2(bucketName: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun headBucket(headBucketRequest: HeadBucketRequest?): HeadBucketResult {
        TODO("Not yet implemented")
    }

    override fun listBuckets(): MutableList<Bucket> {
        TODO("Not yet implemented")
    }

    override fun listBuckets(listBucketsRequest: ListBucketsRequest?): MutableList<Bucket> {
        TODO("Not yet implemented")
    }

    override fun getBucketLocation(bucketName: String?): String {
        TODO("Not yet implemented")
    }

    override fun getBucketLocation(getBucketLocationRequest: GetBucketLocationRequest?): String {
        TODO("Not yet implemented")
    }

    override fun createBucket(createBucketRequest: CreateBucketRequest?): Bucket {
        TODO("Not yet implemented")
    }

    override fun createBucket(bucketName: String?): Bucket {
        TODO("Not yet implemented")
    }

    override fun createBucket(bucketName: String?, region: com.amazonaws.services.s3.model.Region?): Bucket {
        TODO("Not yet implemented")
    }

    override fun createBucket(bucketName: String?, region: String?): Bucket {
        TODO("Not yet implemented")
    }

    override fun getObjectAcl(bucketName: String?, key: String?): AccessControlList {
        TODO("Not yet implemented")
    }

    override fun getObjectAcl(bucketName: String?, key: String?, versionId: String?): AccessControlList {
        TODO("Not yet implemented")
    }

    override fun getObjectAcl(getObjectAclRequest: GetObjectAclRequest?): AccessControlList {
        TODO("Not yet implemented")
    }

    override fun setObjectAcl(bucketName: String?, key: String?, acl: AccessControlList?) {
        TODO("Not yet implemented")
    }

    override fun setObjectAcl(bucketName: String?, key: String?, acl: CannedAccessControlList?) {
        TODO("Not yet implemented")
    }

    override fun setObjectAcl(bucketName: String?, key: String?, versionId: String?, acl: AccessControlList?) {
        TODO("Not yet implemented")
    }

    override fun setObjectAcl(bucketName: String?, key: String?, versionId: String?, acl: CannedAccessControlList?) {
        TODO("Not yet implemented")
    }

    override fun setObjectAcl(setObjectAclRequest: SetObjectAclRequest?) {
        TODO("Not yet implemented")
    }

    override fun getBucketAcl(bucketName: String?): AccessControlList {
        TODO("Not yet implemented")
    }

    override fun getBucketAcl(getBucketAclRequest: GetBucketAclRequest?): AccessControlList {
        TODO("Not yet implemented")
    }

    override fun setBucketAcl(setBucketAclRequest: SetBucketAclRequest?) {
        TODO("Not yet implemented")
    }

    override fun setBucketAcl(bucketName: String?, acl: AccessControlList?) {
        TODO("Not yet implemented")
    }

    override fun setBucketAcl(bucketName: String?, acl: CannedAccessControlList?) {
        TODO("Not yet implemented")
    }

    override fun getObjectMetadata(bucketName: String?, key: String?): ObjectMetadata {
        TODO("Not yet implemented")
    }

    override fun getObjectMetadata(getObjectMetadataRequest: GetObjectMetadataRequest?): ObjectMetadata {
        TODO("Not yet implemented")
    }

    override fun getObjectAsString(bucketName: String?, key: String?): String {
        TODO("Not yet implemented")
    }

    override fun getObjectTagging(getObjectTaggingRequest: GetObjectTaggingRequest?): GetObjectTaggingResult {
        TODO("Not yet implemented")
    }

    override fun setObjectTagging(setObjectTaggingRequest: SetObjectTaggingRequest?): SetObjectTaggingResult {
        TODO("Not yet implemented")
    }

    override fun deleteObjectTagging(deleteObjectTaggingRequest: DeleteObjectTaggingRequest?): DeleteObjectTaggingResult {
        TODO("Not yet implemented")
    }

    override fun deleteBucket(deleteBucketRequest: DeleteBucketRequest?) {
        TODO("Not yet implemented")
    }

    override fun deleteBucket(bucketName: String?) {
        TODO("Not yet implemented")
    }

    override fun copyObject(
        sourceBucketName: String?,
        sourceKey: String?,
        destinationBucketName: String?,
        destinationKey: String?,
    ): CopyObjectResult {
        TODO("Not yet implemented")
    }

    override fun copyObject(copyObjectRequest: CopyObjectRequest?): CopyObjectResult {
        TODO("Not yet implemented")
    }

    override fun deleteObject(bucketName: String?, key: String?) {
        TODO("Not yet implemented")
    }

    override fun deleteObject(deleteObjectRequest: DeleteObjectRequest?) {
        TODO("Not yet implemented")
    }

    override fun deleteObjects(deleteObjectsRequest: DeleteObjectsRequest?): DeleteObjectsResult {
        TODO("Not yet implemented")
    }

    override fun deleteVersion(bucketName: String?, key: String?, versionId: String?) {
        TODO("Not yet implemented")
    }

    override fun deleteVersion(deleteVersionRequest: DeleteVersionRequest?) {
        TODO("Not yet implemented")
    }

    override fun getBucketLoggingConfiguration(bucketName: String?): BucketLoggingConfiguration {
        TODO("Not yet implemented")
    }

    override fun getBucketLoggingConfiguration(getBucketLoggingConfigurationRequest: GetBucketLoggingConfigurationRequest?): BucketLoggingConfiguration {
        TODO("Not yet implemented")
    }

    override fun setBucketLoggingConfiguration(setBucketLoggingConfigurationRequest: SetBucketLoggingConfigurationRequest?) {
        TODO("Not yet implemented")
    }

    override fun getBucketVersioningConfiguration(bucketName: String?): BucketVersioningConfiguration {
        TODO("Not yet implemented")
    }

    override fun getBucketVersioningConfiguration(getBucketVersioningConfigurationRequest: GetBucketVersioningConfigurationRequest?): BucketVersioningConfiguration {
        TODO("Not yet implemented")
    }

    override fun setBucketVersioningConfiguration(setBucketVersioningConfigurationRequest: SetBucketVersioningConfigurationRequest?) {
        TODO("Not yet implemented")
    }

    override fun getBucketLifecycleConfiguration(bucketName: String?): BucketLifecycleConfiguration {
        TODO("Not yet implemented")
    }

    override fun getBucketLifecycleConfiguration(getBucketLifecycleConfigurationRequest: GetBucketLifecycleConfigurationRequest?): BucketLifecycleConfiguration {
        TODO("Not yet implemented")
    }

    override fun setBucketLifecycleConfiguration(
        bucketName: String?,
        bucketLifecycleConfiguration: BucketLifecycleConfiguration?,
    ) {
        TODO("Not yet implemented")
    }

    override fun setBucketLifecycleConfiguration(setBucketLifecycleConfigurationRequest: SetBucketLifecycleConfigurationRequest?) {
        TODO("Not yet implemented")
    }

    override fun deleteBucketLifecycleConfiguration(bucketName: String?) {
        TODO("Not yet implemented")
    }

    override fun deleteBucketLifecycleConfiguration(deleteBucketLifecycleConfigurationRequest: DeleteBucketLifecycleConfigurationRequest?) {
        TODO("Not yet implemented")
    }

    override fun getBucketCrossOriginConfiguration(bucketName: String?): BucketCrossOriginConfiguration {
        TODO("Not yet implemented")
    }

    override fun getBucketCrossOriginConfiguration(getBucketCrossOriginConfigurationRequest: GetBucketCrossOriginConfigurationRequest?): BucketCrossOriginConfiguration {
        TODO("Not yet implemented")
    }

    override fun setBucketCrossOriginConfiguration(
        bucketName: String?,
        bucketCrossOriginConfiguration: BucketCrossOriginConfiguration?,
    ) {
        TODO("Not yet implemented")
    }

    override fun setBucketCrossOriginConfiguration(setBucketCrossOriginConfigurationRequest: SetBucketCrossOriginConfigurationRequest?) {
        TODO("Not yet implemented")
    }

    override fun deleteBucketCrossOriginConfiguration(bucketName: String?) {
        TODO("Not yet implemented")
    }

    override fun deleteBucketCrossOriginConfiguration(deleteBucketCrossOriginConfigurationRequest: DeleteBucketCrossOriginConfigurationRequest?) {
        TODO("Not yet implemented")
    }

    override fun getBucketTaggingConfiguration(bucketName: String?): BucketTaggingConfiguration {
        TODO("Not yet implemented")
    }

    override fun getBucketTaggingConfiguration(getBucketTaggingConfigurationRequest: GetBucketTaggingConfigurationRequest?): BucketTaggingConfiguration {
        TODO("Not yet implemented")
    }

    override fun setBucketTaggingConfiguration(
        bucketName: String?,
        bucketTaggingConfiguration: BucketTaggingConfiguration?,
    ) {
        TODO("Not yet implemented")
    }

    override fun setBucketTaggingConfiguration(setBucketTaggingConfigurationRequest: SetBucketTaggingConfigurationRequest?) {
        TODO("Not yet implemented")
    }

    override fun deleteBucketTaggingConfiguration(bucketName: String?) {
        TODO("Not yet implemented")
    }

    override fun deleteBucketTaggingConfiguration(deleteBucketTaggingConfigurationRequest: DeleteBucketTaggingConfigurationRequest?) {
        TODO("Not yet implemented")
    }

    override fun getBucketNotificationConfiguration(bucketName: String?): BucketNotificationConfiguration {
        TODO("Not yet implemented")
    }

    override fun getBucketNotificationConfiguration(getBucketNotificationConfigurationRequest: GetBucketNotificationConfigurationRequest?): BucketNotificationConfiguration {
        TODO("Not yet implemented")
    }

    override fun setBucketNotificationConfiguration(setBucketNotificationConfigurationRequest: SetBucketNotificationConfigurationRequest?) {
        TODO("Not yet implemented")
    }

    override fun setBucketNotificationConfiguration(
        bucketName: String?,
        bucketNotificationConfiguration: BucketNotificationConfiguration?,
    ) {
        TODO("Not yet implemented")
    }

    override fun getBucketWebsiteConfiguration(bucketName: String?): BucketWebsiteConfiguration {
        TODO("Not yet implemented")
    }

    override fun getBucketWebsiteConfiguration(getBucketWebsiteConfigurationRequest: GetBucketWebsiteConfigurationRequest?): BucketWebsiteConfiguration {
        TODO("Not yet implemented")
    }

    override fun setBucketWebsiteConfiguration(bucketName: String?, configuration: BucketWebsiteConfiguration?) {
        TODO("Not yet implemented")
    }

    override fun setBucketWebsiteConfiguration(setBucketWebsiteConfigurationRequest: SetBucketWebsiteConfigurationRequest?) {
        TODO("Not yet implemented")
    }

    override fun deleteBucketWebsiteConfiguration(bucketName: String?) {
        TODO("Not yet implemented")
    }

    override fun deleteBucketWebsiteConfiguration(deleteBucketWebsiteConfigurationRequest: DeleteBucketWebsiteConfigurationRequest?) {
        TODO("Not yet implemented")
    }

    override fun getBucketPolicy(bucketName: String?): BucketPolicy {
        TODO("Not yet implemented")
    }

    override fun getBucketPolicy(getBucketPolicyRequest: GetBucketPolicyRequest?): BucketPolicy {
        TODO("Not yet implemented")
    }

    override fun setBucketPolicy(bucketName: String?, policyText: String?) {
        TODO("Not yet implemented")
    }

    override fun setBucketPolicy(setBucketPolicyRequest: SetBucketPolicyRequest?) {
        TODO("Not yet implemented")
    }

    override fun deleteBucketPolicy(bucketName: String?) {
        TODO("Not yet implemented")
    }

    override fun deleteBucketPolicy(deleteBucketPolicyRequest: DeleteBucketPolicyRequest?) {
        TODO("Not yet implemented")
    }

    override fun generatePresignedUrl(bucketName: String?, key: String?, expiration: Date?): URL {
        TODO("Not yet implemented")
    }

    override fun generatePresignedUrl(bucketName: String?, key: String?, expiration: Date?, method: HttpMethod?): URL {
        TODO("Not yet implemented")
    }

    override fun generatePresignedUrl(generatePresignedUrlRequest: GeneratePresignedUrlRequest?): URL {
        TODO("Not yet implemented")
    }

    override fun listParts(request: ListPartsRequest?): PartListing {
        TODO("Not yet implemented")
    }

    override fun listMultipartUploads(request: ListMultipartUploadsRequest?): MultipartUploadListing {
        TODO("Not yet implemented")
    }

    override fun getCachedResponseMetadata(request: AmazonWebServiceRequest?): S3ResponseMetadata {
        TODO("Not yet implemented")
    }

    override fun restoreObject(request: RestoreObjectRequest?) {
        TODO("Not yet implemented")
    }

    override fun restoreObject(bucketName: String?, key: String?, expirationInDays: Int) {
        TODO("Not yet implemented")
    }

    override fun restoreObjectV2(request: RestoreObjectRequest?): RestoreObjectResult {
        TODO("Not yet implemented")
    }

    override fun enableRequesterPays(bucketName: String?) {
        TODO("Not yet implemented")
    }

    override fun disableRequesterPays(bucketName: String?) {
        TODO("Not yet implemented")
    }

    override fun isRequesterPaysEnabled(bucketName: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun setRequestPaymentConfiguration(setRequestPaymentConfigurationRequest: SetRequestPaymentConfigurationRequest?) {
        TODO("Not yet implemented")
    }

    override fun setBucketReplicationConfiguration(
        bucketName: String?,
        configuration: BucketReplicationConfiguration?,
    ) {
        TODO("Not yet implemented")
    }

    override fun setBucketReplicationConfiguration(setBucketReplicationConfigurationRequest: SetBucketReplicationConfigurationRequest?) {
        TODO("Not yet implemented")
    }

    override fun getBucketReplicationConfiguration(bucketName: String?): BucketReplicationConfiguration {
        TODO("Not yet implemented")
    }

    override fun getBucketReplicationConfiguration(getBucketReplicationConfigurationRequest: GetBucketReplicationConfigurationRequest?): BucketReplicationConfiguration {
        TODO("Not yet implemented")
    }

    override fun deleteBucketReplicationConfiguration(bucketName: String?) {
        TODO("Not yet implemented")
    }

    override fun deleteBucketReplicationConfiguration(request: DeleteBucketReplicationConfigurationRequest?) {
        TODO("Not yet implemented")
    }

    override fun doesObjectExist(bucketName: String?, objectName: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun getBucketAccelerateConfiguration(bucketName: String?): BucketAccelerateConfiguration {
        TODO("Not yet implemented")
    }

    override fun getBucketAccelerateConfiguration(getBucketAccelerateConfigurationRequest: GetBucketAccelerateConfigurationRequest?): BucketAccelerateConfiguration {
        TODO("Not yet implemented")
    }

    override fun setBucketAccelerateConfiguration(
        bucketName: String?,
        accelerateConfiguration: BucketAccelerateConfiguration?,
    ) {
        TODO("Not yet implemented")
    }

    override fun setBucketAccelerateConfiguration(setBucketAccelerateConfigurationRequest: SetBucketAccelerateConfigurationRequest?) {
        TODO("Not yet implemented")
    }

    override fun deleteBucketMetricsConfiguration(
        bucketName: String?,
        id: String?,
    ): DeleteBucketMetricsConfigurationResult {
        TODO("Not yet implemented")
    }

    override fun deleteBucketMetricsConfiguration(deleteBucketMetricsConfigurationRequest: DeleteBucketMetricsConfigurationRequest?): DeleteBucketMetricsConfigurationResult {
        TODO("Not yet implemented")
    }

    override fun getBucketMetricsConfiguration(bucketName: String?, id: String?): GetBucketMetricsConfigurationResult {
        TODO("Not yet implemented")
    }

    override fun getBucketMetricsConfiguration(getBucketMetricsConfigurationRequest: GetBucketMetricsConfigurationRequest?): GetBucketMetricsConfigurationResult {
        TODO("Not yet implemented")
    }

    override fun setBucketMetricsConfiguration(
        bucketName: String?,
        metricsConfiguration: MetricsConfiguration?,
    ): SetBucketMetricsConfigurationResult {
        TODO("Not yet implemented")
    }

    override fun setBucketMetricsConfiguration(setBucketMetricsConfigurationRequest: SetBucketMetricsConfigurationRequest?): SetBucketMetricsConfigurationResult {
        TODO("Not yet implemented")
    }

    override fun listBucketMetricsConfigurations(listBucketMetricsConfigurationsRequest: ListBucketMetricsConfigurationsRequest?): ListBucketMetricsConfigurationsResult {
        TODO("Not yet implemented")
    }

    override fun deleteBucketOwnershipControls(deleteBucketOwnershipControlsRequest: DeleteBucketOwnershipControlsRequest?): DeleteBucketOwnershipControlsResult {
        TODO("Not yet implemented")
    }

    override fun getBucketOwnershipControls(getBucketOwnershipControlsRequest: GetBucketOwnershipControlsRequest?): GetBucketOwnershipControlsResult {
        TODO("Not yet implemented")
    }

    override fun setBucketOwnershipControls(
        bucketName: String?,
        ownershipControls: OwnershipControls?,
    ): SetBucketOwnershipControlsResult {
        TODO("Not yet implemented")
    }

    override fun setBucketOwnershipControls(setBucketOwnershipControlsRequest: SetBucketOwnershipControlsRequest?): SetBucketOwnershipControlsResult {
        TODO("Not yet implemented")
    }

    override fun deleteBucketAnalyticsConfiguration(
        bucketName: String?,
        id: String?,
    ): DeleteBucketAnalyticsConfigurationResult {
        TODO("Not yet implemented")
    }

    override fun deleteBucketAnalyticsConfiguration(deleteBucketAnalyticsConfigurationRequest: DeleteBucketAnalyticsConfigurationRequest?): DeleteBucketAnalyticsConfigurationResult {
        TODO("Not yet implemented")
    }

    override fun getBucketAnalyticsConfiguration(
        bucketName: String?,
        id: String?,
    ): GetBucketAnalyticsConfigurationResult {
        TODO("Not yet implemented")
    }

    override fun getBucketAnalyticsConfiguration(getBucketAnalyticsConfigurationRequest: GetBucketAnalyticsConfigurationRequest?): GetBucketAnalyticsConfigurationResult {
        TODO("Not yet implemented")
    }

    override fun setBucketAnalyticsConfiguration(
        bucketName: String?,
        analyticsConfiguration: AnalyticsConfiguration?,
    ): SetBucketAnalyticsConfigurationResult {
        TODO("Not yet implemented")
    }

    override fun setBucketAnalyticsConfiguration(setBucketAnalyticsConfigurationRequest: SetBucketAnalyticsConfigurationRequest?): SetBucketAnalyticsConfigurationResult {
        TODO("Not yet implemented")
    }

    override fun listBucketAnalyticsConfigurations(listBucketAnalyticsConfigurationsRequest: ListBucketAnalyticsConfigurationsRequest?): ListBucketAnalyticsConfigurationsResult {
        TODO("Not yet implemented")
    }

    override fun deleteBucketIntelligentTieringConfiguration(
        bucketName: String?,
        id: String?,
    ): DeleteBucketIntelligentTieringConfigurationResult {
        TODO("Not yet implemented")
    }

    override fun deleteBucketIntelligentTieringConfiguration(deleteBucketIntelligentTieringConfigurationRequest: DeleteBucketIntelligentTieringConfigurationRequest?): DeleteBucketIntelligentTieringConfigurationResult {
        TODO("Not yet implemented")
    }

    override fun getBucketIntelligentTieringConfiguration(
        bucketName: String?,
        id: String?,
    ): GetBucketIntelligentTieringConfigurationResult {
        TODO("Not yet implemented")
    }

    override fun getBucketIntelligentTieringConfiguration(getBucketIntelligentTieringConfigurationRequest: GetBucketIntelligentTieringConfigurationRequest?): GetBucketIntelligentTieringConfigurationResult {
        TODO("Not yet implemented")
    }

    override fun setBucketIntelligentTieringConfiguration(
        bucketName: String?,
        intelligentTieringConfiguration: IntelligentTieringConfiguration?,
    ): SetBucketIntelligentTieringConfigurationResult {
        TODO("Not yet implemented")
    }

    override fun setBucketIntelligentTieringConfiguration(setBucketIntelligentTieringConfigurationRequest: SetBucketIntelligentTieringConfigurationRequest?): SetBucketIntelligentTieringConfigurationResult {
        TODO("Not yet implemented")
    }

    override fun listBucketIntelligentTieringConfigurations(listBucketIntelligentTieringConfigurationsRequest: ListBucketIntelligentTieringConfigurationsRequest?): ListBucketIntelligentTieringConfigurationsResult {
        TODO("Not yet implemented")
    }

    override fun deleteBucketInventoryConfiguration(
        bucketName: String?,
        id: String?,
    ): DeleteBucketInventoryConfigurationResult {
        TODO("Not yet implemented")
    }

    override fun deleteBucketInventoryConfiguration(deleteBucketInventoryConfigurationRequest: DeleteBucketInventoryConfigurationRequest?): DeleteBucketInventoryConfigurationResult {
        TODO("Not yet implemented")
    }

    override fun getBucketInventoryConfiguration(
        bucketName: String?,
        id: String?,
    ): GetBucketInventoryConfigurationResult {
        TODO("Not yet implemented")
    }

    override fun getBucketInventoryConfiguration(getBucketInventoryConfigurationRequest: GetBucketInventoryConfigurationRequest?): GetBucketInventoryConfigurationResult {
        TODO("Not yet implemented")
    }

    override fun setBucketInventoryConfiguration(
        bucketName: String?,
        inventoryConfiguration: InventoryConfiguration?,
    ): SetBucketInventoryConfigurationResult {
        TODO("Not yet implemented")
    }

    override fun setBucketInventoryConfiguration(setBucketInventoryConfigurationRequest: SetBucketInventoryConfigurationRequest?): SetBucketInventoryConfigurationResult {
        TODO("Not yet implemented")
    }

    override fun listBucketInventoryConfigurations(listBucketInventoryConfigurationsRequest: ListBucketInventoryConfigurationsRequest?): ListBucketInventoryConfigurationsResult {
        TODO("Not yet implemented")
    }

    override fun deleteBucketEncryption(bucketName: String?): DeleteBucketEncryptionResult {
        TODO("Not yet implemented")
    }

    override fun deleteBucketEncryption(request: DeleteBucketEncryptionRequest?): DeleteBucketEncryptionResult {
        TODO("Not yet implemented")
    }

    override fun getBucketEncryption(bucketName: String?): GetBucketEncryptionResult {
        TODO("Not yet implemented")
    }

    override fun getBucketEncryption(request: GetBucketEncryptionRequest?): GetBucketEncryptionResult {
        TODO("Not yet implemented")
    }

    override fun setBucketEncryption(setBucketEncryptionRequest: SetBucketEncryptionRequest?): SetBucketEncryptionResult {
        TODO("Not yet implemented")
    }

    override fun setPublicAccessBlock(request: SetPublicAccessBlockRequest?): SetPublicAccessBlockResult {
        TODO("Not yet implemented")
    }

    override fun getPublicAccessBlock(request: GetPublicAccessBlockRequest?): GetPublicAccessBlockResult {
        TODO("Not yet implemented")
    }

    override fun deletePublicAccessBlock(request: DeletePublicAccessBlockRequest?): DeletePublicAccessBlockResult {
        TODO("Not yet implemented")
    }

    override fun getBucketPolicyStatus(request: GetBucketPolicyStatusRequest?): GetBucketPolicyStatusResult {
        TODO("Not yet implemented")
    }

    override fun selectObjectContent(selectRequest: SelectObjectContentRequest?): SelectObjectContentResult {
        TODO("Not yet implemented")
    }

    override fun setObjectLegalHold(setObjectLegalHoldRequest: SetObjectLegalHoldRequest?): SetObjectLegalHoldResult {
        TODO("Not yet implemented")
    }

    override fun getObjectLegalHold(getObjectLegalHoldRequest: GetObjectLegalHoldRequest?): GetObjectLegalHoldResult {
        TODO("Not yet implemented")
    }

    override fun setObjectLockConfiguration(setObjectLockConfigurationRequest: SetObjectLockConfigurationRequest?): SetObjectLockConfigurationResult {
        TODO("Not yet implemented")
    }

    override fun getObjectLockConfiguration(getObjectLockConfigurationRequest: GetObjectLockConfigurationRequest?): GetObjectLockConfigurationResult {
        TODO("Not yet implemented")
    }

    override fun setObjectRetention(setObjectRetentionRequest: SetObjectRetentionRequest?): SetObjectRetentionResult {
        TODO("Not yet implemented")
    }

    override fun getObjectRetention(getObjectRetentionRequest: GetObjectRetentionRequest?): GetObjectRetentionResult {
        TODO("Not yet implemented")
    }

    override fun writeGetObjectResponse(writeGetObjectResponseRequest: WriteGetObjectResponseRequest?): WriteGetObjectResponseResult {
        TODO("Not yet implemented")
    }

    override fun download(presignedUrlDownloadRequest: PresignedUrlDownloadRequest?): PresignedUrlDownloadResult {
        TODO("Not yet implemented")
    }

    override fun download(presignedUrlDownloadRequest: PresignedUrlDownloadRequest?, destinationFile: File?) {
        TODO("Not yet implemented")
    }

    override fun upload(presignedUrlUploadRequest: PresignedUrlUploadRequest?): PresignedUrlUploadResult {
        TODO("Not yet implemented")
    }

    override fun shutdown() {
        TODO("Not yet implemented")
    }

    override fun getRegion(): com.amazonaws.services.s3.model.Region {
        TODO("Not yet implemented")
    }

    override fun getRegionName(): String {
        TODO("Not yet implemented")
    }

    override fun getUrl(bucketName: String?, key: String?): URL {
        return URL("https://storedUrl.com/$key")
    }

    override fun waiters(): AmazonS3Waiters {
        TODO("Not yet implemented")
    }
}
